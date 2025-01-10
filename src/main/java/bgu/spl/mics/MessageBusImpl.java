package bgu.spl.mics;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.services.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static class MessageBusImplHolder {
        private static MessageBusImpl instance = new MessageBusImpl();

    }

    public static MessageBusImpl getInstance() {
        return MessageBusImplHolder.instance;
    }

    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> mServiceSubs;
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> mServiceMsgsQs;
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> messageSubMap;
    private ConcurrentHashMap<Event, Future> eventFutureMap;

    private MessageBusImpl() {
        mServiceMsgsQs = new ConcurrentHashMap<>();
        mServiceSubs = new ConcurrentHashMap<>();
        messageSubMap = new ConcurrentHashMap<>();
        eventFutureMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        messageSubMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        ConcurrentLinkedQueue<MicroService> subscribers = messageSubMap.get(type);
        ConcurrentLinkedQueue<Class<? extends Message>> classes = (mServiceSubs.get(m));

        synchronized (classes) {
            classes.add(type);
        }

        synchronized (type) {
            subscribers.add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        messageSubMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        ConcurrentLinkedQueue<MicroService> subscribers = messageSubMap.get(type);
        ConcurrentLinkedQueue<Class<? extends Message>> classes = (mServiceSubs.get(m));

        synchronized (classes) {
            classes.add(type);
        }

        synchronized (type) {
            subscribers.add(m);
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> f = eventFutureMap.get(e);
        f.resolve(result);
        eventFutureMap.remove(e);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (messageSubMap.containsKey(b.getClass())) {
            ConcurrentLinkedQueue<MicroService> currMicroServices = messageSubMap.get(b.getClass());
            for (MicroService m : currMicroServices) {
                ConcurrentLinkedQueue<Message> d = mServiceMsgsQs.get(m);
                if (d != null) {
                    if (b instanceof CrashedBroadcast) {
                        if (m instanceof TimeService || m instanceof PoseService) {
                            synchronized (m) {
                                d.clear();
                            }
                        }
                    }
                    synchronized (m) {
                        d.add(b);
                    }
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        ConcurrentLinkedQueue<MicroService> subbedMServices = messageSubMap.get(e.getClass());
        if (subbedMServices == null || subbedMServices.isEmpty())
            return null;
        synchronized (e.getClass()) {
            MicroService m = subbedMServices.poll();
            synchronized (m) {
                ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
                if (mServiceMsgQ != null) {
                    mServiceMsgQ.add(e);
                    subbedMServices.add(m); // return the mService to the back of the queue
                } else {
                    System.out.println(e.getClass() + " " + e);
                }
            }
        }

        Future<T> f = new Future<>();
        eventFutureMap.putIfAbsent(e, f);
        return f;
    }

    @Override
    public void register(MicroService m) {
        ConcurrentLinkedQueue<Class<? extends Message>> mServiceSubscribtionSet = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Message> mServiceMsgQueue = new ConcurrentLinkedQueue<>();

        mServiceSubs.putIfAbsent(m, mServiceSubscribtionSet);
        mServiceMsgsQs.putIfAbsent(m, mServiceMsgQueue);
    }

    @Override
    public void unregister(MicroService m) {

        // synchronized (m) { could also be here
        ConcurrentLinkedQueue<Class<? extends Message>> subs = mServiceSubs.get(m);
        for (Class<? extends Message> msg : subs) {
            synchronized (msg) {
                ConcurrentLinkedQueue<MicroService> broadcastSubs = messageSubMap.get(msg);
                broadcastSubs.remove(m); // dont care about order

            }
        }
        synchronized (m) {
            mServiceSubs.remove(m);
            mServiceMsgsQs.remove(m);
        }

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        Message msg;
        if (mServiceSubs.get(m).isEmpty()) {
            throw new IllegalStateException(m.getName() + " hasn't registered!");
        }
        ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
        return mServiceMsgQ.poll();
    }

    /**
     * Getters, added for tests
     */
    public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> mServiceSubs() {
        return mServiceSubs;
    }

    public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> mServiceMsgsQs() {
        return mServiceMsgsQs;
    }

    public ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> broadcastSubMap() {
        return messageSubMap;
    }


    public ConcurrentHashMap<Event, Future> eventFutureMap() {
        return eventFutureMap;
    }

    public ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> messageSubMap() {return messageSubMap;}

    /**
     * For cleanup tests
     */
    public void cleanup(){
        mServiceMsgsQs.clear();
        messageSubMap.clear();
        mServiceSubs.clear();
        eventFutureMap.clear();
    }
}
