package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	/**
	 * Hash Maps
	 */
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceToMessageQueue = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<? extends Event> ,ConcurrentLinkedQueue<MicroService>> eventToQueue = new ConcurrentHashMap<>(); 	//Hash that holds for each Event type a queue of microServices that can handel with it, it means get it.
	private ConcurrentHashMap<Class<? extends Broadcast> ,ConcurrentLinkedQueue<MicroService>> broadcastToQueue = new ConcurrentHashMap<>();	//Hash that holds for each Broadcast type list of MicroServices that are willing to get it.
	private ConcurrentHashMap<Event, Future> eventToFuture = new ConcurrentHashMap<>();

	/**
	 * Locks
	 */
	private final Object blockAddEvent = new Object();
	private final Object blockAddBroadcast = new Object();

	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		//Init
	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (blockAddEvent) {
			if (eventToQueue.get(type) == null) { //if the type of this event is not already handle.
				eventToQueue.put(type, new ConcurrentLinkedQueue<>());
			}
			eventToQueue.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (blockAddBroadcast) {
			if (broadcastToQueue.get(type) == null) { //if the type of this Broadcast is not already handle.
				broadcastToQueue.put(type, new ConcurrentLinkedQueue<>());
			}
		}
			broadcastToQueue.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventToFuture.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (blockAddBroadcast) {
			for (int i = 0; i < broadcastToQueue.get(b.getClass()).size(); i++) {
				if (microServiceToMessageQueue.get(broadcastToQueue.get(b.getClass())) == null) {
					microServiceToMessageQueue.put(broadcastToQueue.get(b.getClass()).peek(), new ConcurrentLinkedQueue<>());
				}
				microServiceToMessageQueue.get(broadcastToQueue.get(b.getClass()).poll()).add(b);
			}
			notifyAll();
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//synchronized (blockAddEvent){   //I want it to be sync on eventToQueue.get(e.getClass())) { //Maybe should change to toString. Maybe don't need it.
				if (eventToQueue.get(e.getClass()) == null) {
					return null;
				}
				Future<T> futureObj = new Future<>();
				eventToFuture.put(e, futureObj);
				MicroService m = eventToQueue.get(e.getClass()).poll();
				eventToQueue.get(e.getClass()).add(m);
				microServiceToMessageQueue.get(m).add(e);
				notifyAll();
				return futureObj;
		//}
	}

	@Override
	public void register(MicroService m) {
		synchronized (blockAddBroadcast) {
			synchronized (blockAddEvent) {
				microServiceToMessageQueue.put(m, new ConcurrentLinkedQueue());
			}
		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (blockAddBroadcast) {
			synchronized (blockAddEvent) {
				ConcurrentLinkedQueue tempQ;
				tempQ = microServiceToMessageQueue.get(m);//Does we get here a specific key or we have many keys that is the same as the class of the instance of the MS m?
				if (tempQ == null) {
					return;
				}
				Event e;//Every message goes to garbage.
				while (!tempQ.isEmpty()) {
					e = (Event) tempQ.poll();
				}
				//if some queue that contains micro services that can handle with some event type is empty we should delete it from the map.

			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		System.out.println("Enter awaitMessage");
		synchronized (blockAddBroadcast) {
			System.out.println("awaitMessage: aquire blockAddBraodcast");
			//synchronized (blockAddEvent) {
				synchronized (m) {
					//System.out.println("awaitMessage: aquire blockAddEvent");
					while (microServiceToMessageQueue.get(m).peek() == null) {
						m.wait();
						System.out.println("Already waited");
					}
				}
			//}
		}
		return microServiceToMessageQueue.get(m).poll();
	}

}
