package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl(){
		//Init
	}
	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
		//Why exist return null;?
	}

	private ConcurrentHashMap<MicroService ,ConcurrentLinkedQueue<Event>> microServiceToQueue = new ConcurrentHashMap<>();//Hash that holds the microServices and their event queue
	private ConcurrentHashMap<Class<? extends Event> ,ConcurrentLinkedQueue<MicroService>> eventToQueue = new ConcurrentHashMap<>(); 	//Hash that holds for each Event type a queue of microServices that can handel with it, it means get it.
	private ConcurrentHashMap<Class<? extends Broadcast> ,ConcurrentLinkedQueue<MicroService>> broadcastToQueue = new ConcurrentHashMap<>();	//Hash that holds for each Broadcast type list of MicroServices that are willing to get it.

	private ConcurrentHashMap<Event, Future> eventToFuture = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> serviceToMessageQueue = new ConcurrentHashMap<>();

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
	public void sendBroadcast(Broadcast b) {
		for(int i = 0 ; i < broadcastToQueue.get(b).size(); i++) {
			if(serviceToMessageQueue.get(broadcastToQueue.get(b).peek()) == null){
				serviceToMessageQueue.put(broadcastToQueue.get(b).peek(), new ConcurrentLinkedQueue<>());
			}
			serviceToMessageQueue.get(broadcastToQueue.get(b).poll()).add(b);
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(eventToQueue.get(e) == null){
			return null;
		}
		Future<T> futureObj = new Future<>();
		eventToFuture.put(e, futureObj);
		MicroService m = eventToQueue.get(e).poll();
		eventToQueue.get(e).add(m);
		microServiceToQueue.get(m).add(e);
		return futureObj;
	}

	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue concQ = new ConcurrentLinkedQueue();
		microServiceToQueue.put(m ,concQ);
	}

	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue tempQ;
		tempQ = microServiceToQueue.get(m);//Does we get here a specific key or we have many keys that is the same as the class of the instance of the MS m?
		if(tempQ == null){
			return;
		}
		Event e;
		while(!tempQ.isEmpty()){
			e = (Event)tempQ.poll();

		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
