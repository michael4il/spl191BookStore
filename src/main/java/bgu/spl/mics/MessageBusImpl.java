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


	//private AtomicReference<Class<? extends Event>> refQueueEvents = new AtomicReference<>(null);
	@Override
	//Should be synch
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(eventToQueue.get(type) == null){ //if the type of this event is not already handle.
			eventToQueue.put(type ,new ConcurrentLinkedQueue<>());

		}
		eventToQueue.get(type).add(m);
	}

	@Override
	//Should be synch
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(broadcastToQueue.get(type) == null){ //if the type of this Broadcast is not already handle.
			broadcastToQueue.put(type, new ConcurrentLinkedQueue<>());
		}
		broadcastToQueue.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventToFuture.get(e).resolve(result);
	}

	@Override
	public void  sendBroadcast(Broadcast b) {
//		for(int i = 0 ; i < broadcastToQueue.get(b.getClass()).size(); i++) {
//			if(serviceToQueue.get(broadcastToQueue.get(b.getClass()).peek()) == null){
//				serviceToQueue.put(broadcastToQueue.get(b.getClass()).peek(), new ConcurrentLinkedQueue<>());
//			}
//			serviceToQueue.get(broadcastToQueue.get(b.getClass()).poll()).add(b);
//		}
		for(MicroService q: broadcastToQueue.get(b.getClass())) {
			synchronized (serviceToQueue.get(q)) {
				serviceToQueue.get(q).add(b);
				serviceToQueue.get(q).notify();
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(eventToQueue.get(e.getClass()) == null||eventToQueue.get(e.getClass()).isEmpty()){// or empty
			return null;
		}
		Future<T> futureObj = new Future<>();
		eventToFuture.put(e, futureObj);
		MicroService m;
		//We should check here the round robbin.
		eventToQueue.get(e.getClass()).add(m = eventToQueue.get(e.getClass()).poll());
		synchronized (serviceToQueue.get(m)) {
			serviceToQueue.get(m).add(e);
			serviceToQueue.get(m).notifyAll();
		}
		return futureObj;
	}

	//Maybe the new make it not able to reach.
	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue concQ = new ConcurrentLinkedQueue();
		serviceToQueue.put(m ,concQ);
	}

	private boolean checkIdentity(MicroService m, MicroService m2) {
		if(m == m2)
			return true;
		return false;
	}
	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue tempQ;
		tempQ = serviceToQueue.get(m);//Does we get here a specific key or we have many keys that is the same as the class of the instance of the MS m?
		if(tempQ == null){
			return;
		}
//
		eventToQueue.forEach((ev,qu) -> qu.forEach(ms-> {if(ms == m) qu.remove(m);} ));
//		for(int i=0;i<eventToQueue.size();i++)

		synchronized (serviceToQueue.get(m)) {
			while (!tempQ.isEmpty()) {
				tempQ.remove();
			}
		}
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
