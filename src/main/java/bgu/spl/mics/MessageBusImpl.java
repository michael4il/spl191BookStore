package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService ,ConcurrentLinkedQueue<Message>> serviceToQueue = new ConcurrentHashMap<>();//Hash that holds the microServices and their event queue
	private ConcurrentHashMap<Class<? extends Event> ,ConcurrentLinkedQueue<MicroService>> eventToQueue = new ConcurrentHashMap<>(); 	//Hash that holds for each Event type a queue of microServices that can handel with it, it means get it.
	private ConcurrentHashMap<Class<? extends Broadcast> ,ConcurrentLinkedQueue<MicroService>> broadcastToQueue = new ConcurrentHashMap<>();	//Hash that holds for each Broadcast type list of MicroServices that are willing to get it.
	private ConcurrentHashMap<Event, Future> eventToFuture = new ConcurrentHashMap<>();

	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		//Init
	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type.getName()) {
			if (eventToQueue.get(type) == null) {//if the type of this event is not already handle.
				eventToQueue.put(type, new ConcurrentLinkedQueue<>());
			}
		}
		eventToQueue.get(type).add(m);
	}

	@SuppressWarnings("Duplicates")
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type.getName()) {
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
	public void  sendBroadcast(Broadcast b) {

		synchronized (b.getClass().getName()) {
			if (broadcastToQueue.get(b.getClass()) == null || broadcastToQueue.get(b.getClass()).isEmpty()) {
				return;
			}
		}
		broadcastToQueue.get(b.getClass()).forEach(microService -> {
			synchronized (serviceToQueue.get(microService)) {
				serviceToQueue.get(microService).add(b);
				serviceToQueue.get(microService).notify();
			}
		});

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m;
		synchronized (e.getClass().getName()) {
			if(eventToQueue.get(e.getClass()) == null || eventToQueue.get(e.getClass()).isEmpty()){// or empty
				return null;
			}
			eventToQueue.get(e.getClass()).add(m = eventToQueue.get(e.getClass()).poll());
		}
		Future<T> futureObj = new Future<>();
		eventToFuture.put(e, futureObj);
		synchronized (serviceToQueue.get(m)) {
			serviceToQueue.get(m).add(e);
			serviceToQueue.get(m).notifyAll();
		}

		System.out.println("EVENT SENT ="+ e.getClass().getSimpleName());
		return futureObj;
	}

	@Override
	//Should be sync?
	public void register(MicroService m) {
		ConcurrentLinkedQueue concQ = new ConcurrentLinkedQueue();
		serviceToQueue.put(m ,concQ);
	}

	@Override
	//Should be sync?
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue tempQ;
		tempQ = serviceToQueue.get(m);
		if(tempQ == null){
			return;
		}
		eventToQueue.forEach((ev,qu) -> qu.forEach(ms-> {
			if(ms == m){
				qu.remove(m);
			}
		}));
		broadcastToQueue.forEach((ev,qu) -> qu.forEach(ms-> {
			if(ms == m){
				qu.remove(m);
			}
		}));
		//Should be sync on serviceToQueue.get(m)? does not make sense because how we will free the key of something that is not exist?
		serviceToQueue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (serviceToQueue.get(m)) {
			while (serviceToQueue.get(m).isEmpty()) {
				serviceToQueue.get(m).wait();
			}
			return serviceToQueue.get(m).poll();
		}
	}

}