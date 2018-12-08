package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

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
	//Hash that holds the microServices and their event queue
	private ConcurrentHashMap<MicroService ,ConcurrentLinkedQueue<Event>> concHashMicroService = new ConcurrentHashMap<>();//Keys are the name of the service. Values are MicroServices.
	//Hash that holds for each Event type a queue of microServices that can handel with it, it means get it.
	private ConcurrentHashMap<Class<? extends Event> ,ConcurrentLinkedQueue<MicroService>> concHashEvent = new ConcurrentHashMap<>();
	//Hash that holds for each Broadcast type list of MicroServices that are willing to get it.
	private ConcurrentHashMap<Class<? extends Broadcast> ,ConcurrentLinkedQueue<MicroService>> concHashBroadcast = new ConcurrentHashMap<>();
	//private AtomicReference<Class<? extends Event>> refQueueEvents = new AtomicReference<>(null);

	@Override
	//Should be synch
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(concHashEvent.get(type) == null){ //if the type of this event is not already handle.
			concHashEvent.put(type ,new ConcurrentLinkedQueue<>());
		}
		concHashEvent.get(type).add(m);
	}

	@Override
	//Should be synch
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(concHashBroadcast.get(type) == null){ //if the type of this Broadcast is not already handle.
			concHashBroadcast.put(type, new ConcurrentLinkedQueue<>());
		}
		concHashBroadcast.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		return null;
	}

	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue concQ = new ConcurrentLinkedQueue();
		concHashMicroService.put(m ,concQ);
	}

	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue tempQ;
		tempQ = concHashMicroService.get(m);//Does we get here a specific key or we have many keys that is the same as the class of the instance of the MS m?
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
