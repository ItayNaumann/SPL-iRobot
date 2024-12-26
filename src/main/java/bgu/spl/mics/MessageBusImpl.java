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

	private static MessageBusImpl instance = null;

	private ConcurrentHashMap<MicroService, ConcurrentSkipListSet<Class<? extends Message>>> mServiceSubs;
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> mServiceMsgsQs;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubsMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentSkipListSet<MicroService>> broadcastSubMap;
	private ConcurrentHashMap<Event, Future> eventFutureMap;

	private MessageBusImpl() {
		mServiceMsgsQs = new ConcurrentHashMap<>();
		mServiceSubs = new ConcurrentHashMap<>();
		broadcastSubMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		eventSubsMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
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
		broadcastSubMap.putIfAbsent(type, new ConcurrentSkipListSet<>());
		ConcurrentSkipListSet<MicroService> subscribers = broadcastSubMap.get(type);
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
		ConcurrentSkipListSet<MicroService> currMicroServices = broadcastSubMap.get(b.getClass());
		for (MicroService m : currMicroServices) {
			synchronized (m) {
				mServiceMsgsQs.get(m).add(b);
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
		ConcurrentSkipListSet<Class<? extends Message>> mServiceSubscribtionSet = new ConcurrentSkipListSet<>();
		ConcurrentLinkedQueue<Message> mServiceMsgQueue = new ConcurrentLinkedQueue<>();

		mServiceSubs.putIfAbsent(m, mServiceSubscribtionSet);
		mServiceMsgsQs.putIfAbsent(m, mServiceMsgQueue);
	}

	@Override
	public void unregister(MicroService m) {

		// synchronized (m) { could also be here
		ConcurrentSkipListSet<Class<? extends Message>> subs = mServiceSubs.get(m);
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
					ConcurrentSkipListSet<MicroService> broadcastSubs = broadcastSubMap.get(broadcastClass);
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
		do {
			synchronized (m) {
				msg = mServiceMsgQ.poll();
			}
		} while (msg == null);

		return msg;
	}

}
