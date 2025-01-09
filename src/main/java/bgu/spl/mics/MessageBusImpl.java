package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

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
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubsMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubMap;
	private ConcurrentHashMap<Event, Future> eventFutureMap;

	private MessageBusImpl() {
		mServiceMsgsQs = new ConcurrentHashMap<>();
		mServiceSubs = new ConcurrentHashMap<>();
		broadcastSubMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		eventSubsMap = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubsMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubsMap.get(type);
		synchronized (type) {
			subscribers.add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		ConcurrentLinkedQueue<MicroService> subscribers = broadcastSubMap.get(type);
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
		if (broadcastSubMap.containsKey(b.getClass())) {
			ConcurrentLinkedQueue<MicroService> currMicroServices = broadcastSubMap.get(b.getClass());
			for (MicroService m : currMicroServices) {
				synchronized (m) {
					mServiceMsgsQs.get(m).add(b);
					System.out.println(m.getName() + "gets msg's Type: " + b.getClass().getName());
					System.out.println(m.getName() + " msgs num: " + mServiceMsgsQs.size());
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> subbedMServices = eventSubsMap.get(e.getClass());
		if (subbedMServices == null || subbedMServices.isEmpty())
			return null;
		synchronized (e.getClass()) {
			MicroService m = subbedMServices.poll();
			synchronized (m) {
				ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
				mServiceMsgQ.add(e);
				subbedMServices.add(m); // return the mService to the back of the queue
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
		for (Class<? extends Message> sub : subs) {
			if (Event.class.isInstance(sub)) {

				Class<? extends Event> eventClass = (Class<? extends Event>) sub;
				synchronized (eventClass) {
					ConcurrentLinkedQueue<MicroService> eventSubs = eventSubsMap.get(eventClass);
					eventSubs.remove(m); // keeps the queue's order
				}
			} else {
				Class<? extends Broadcast> broadcastClass = (Class<? extends Broadcast>) sub;
				synchronized (broadcastClass) {
					ConcurrentLinkedQueue<MicroService> broadcastSubs = broadcastSubMap.get(broadcastClass);
					broadcastSubs.remove(m); // dont care about order
				}
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
		ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
		return mServiceMsgQ.poll();
	}

	/**
	 * Getters, added for tests
	 */
	public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> mServiceSubs(){
		return mServiceSubs;
	}
	public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> mServiceMsgsQs(){ return mServiceMsgsQs; }

	public ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubMap() {
		return broadcastSubMap;
	}

	public  ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubsMap(){ return eventSubsMap; }

	public ConcurrentHashMap<Event, Future> eventFutureMap(){ return eventFutureMap; }
}
