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
		eventSubsMap = new ConcurrentHashMap<>();
		broadcastSubMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubsMap.get(type);
		if (subscribers == null) {
			subscribers = new ConcurrentLinkedQueue<>();
			eventSubsMap.put(type, subscribers);
		}
		subscribers.add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		ConcurrentSkipListSet<MicroService> subscribers = broadcastSubMap.get(type);
		if (subscribers == null) {
			subscribers = new ConcurrentSkipListSet<>();
			broadcastSubMap.put(type, subscribers);
		}
		subscribers.add(m);
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
			mServiceMsgsQs.get(m).add(b);
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> subbedMServices = eventSubsMap.get(e.getClass());
		if (subbedMServices == null || subbedMServices.isEmpty())
			return null;
		MicroService m = subbedMServices.poll();
		ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
		mServiceMsgQ.add(e);
		subbedMServices.add(m); // return the mService to the back of the queue

		Future<T> f = new Future<>();
		eventFutureMap.put(e, f);
		return f;
	}

	@Override
	public void register(MicroService m) {
		ConcurrentSkipListSet<Class<? extends Message>> mServiceSubscribtionSet = new ConcurrentSkipListSet<>();
		mServiceSubs.put(m, mServiceSubscribtionSet);

		ConcurrentLinkedQueue<Message> mServiceMsgQueue = new ConcurrentLinkedQueue<>();
		mServiceMsgsQs.put(m, mServiceMsgQueue);
	}

	@Override
	public void unregister(MicroService m) {
		mServiceMsgsQs.remove(m);

		ConcurrentSkipListSet<Class<? extends Message>> subs = mServiceSubs.get(m);
		for (Class<? extends Message> sub : subs) {
			if (Event.class.isInstance(sub)) {
				Class<? extends Event> eventClass = (Class<? extends Event>) sub;
				ConcurrentLinkedQueue<MicroService> eventSubs = eventSubsMap.get(eventClass);
				eventSubs.remove(m); // keeps the queue's order
			} else {
				Class<? extends Broadcast> broadcastClass = (Class<? extends Broadcast>) sub;
				ConcurrentSkipListSet<MicroService> broadcastSubs = broadcastSubMap.get(broadcastClass);
				broadcastSubs.remove(m); // dont care about order
			}
		}
		mServiceSubs.remove(m);

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message msg;
		ConcurrentLinkedQueue<Message> mServiceMsgQ = mServiceMsgsQs.get(m);
		do {
			msg = mServiceMsgQ.poll();

		} while (msg == null);

		return msg;
	}

}
