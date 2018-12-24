package bgu.spl.mics;

import bgu.spl.mics.Messages.Broadcasts.Tick;

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
		//Sync because me made a new queue when we get a new event/braodcast. we don't want that more than 1 will create the same key with different queues.
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
		//Sync because me made a new queue when we get a new event/braodcast. we don't want that more than 1 will create the same key with different queues.
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
		//Same as sendEvent.
		synchronized (b.getClass().getName()) { // in case the there's no queue available
			if (broadcastToQueue.get(b.getClass()) == null || broadcastToQueue.get(b.getClass()).isEmpty()) {
				return;
			}
		}
		broadcastToQueue.get(b.getClass()).forEach(microService -> {
			try {
				synchronized (microService) { // hold monitor to notify
					serviceToQueue.get(microService).add(b);
					microService.notify();
				}
			}catch (NullPointerException ex){}
		});


		if(b.getClass() == Tick.class){ // last tick resolve all events
			Tick tick = (Tick) b;
			if(tick.getLast()){
				eventToFuture.forEach((K, V) -> {
					V.resolve(null);
				});
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m;
		try {
			//Sync for the Round robin - we don't want that one thread will poll and than other thread also,
			//and than the second will add before the first.
			//Sync on "if" - maybe in some programs subscribe to event has to be before sendEvent, and therefore without the sync
			//and important - this is the same sync for subscribe event, it could yeild that the program is not correct.
			synchronized (e.getClass().getName()) {//hold event type monitor
				if (eventToQueue.get(e.getClass()) == null || eventToQueue.get(e.getClass()).isEmpty()) {// in case the there's no queue available
					return null;
				}
				eventToQueue.get(e.getClass()).add(m = eventToQueue.get(e.getClass()).poll());//round robin first to last + save reference
			}
		}catch (NullPointerException ex2){return null;}

		Future<T> futureObj = new Future<>();
		eventToFuture.put(e, futureObj);

		try {
			//We sync because we want to notify to the thread of the MicroService that an event came.
			synchronized (m) {
				serviceToQueue.get(m).add(e);
				m.notifyAll();
			}
		}catch (NullPointerException ex1){return null;}
		return futureObj;
	}

	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue concQ = new ConcurrentLinkedQueue();
		serviceToQueue.put(m ,concQ);
	}

	@Override

	public void unregister(MicroService m) {
		ConcurrentLinkedQueue tempQ;
		tempQ = serviceToQueue.get(m);
		if(tempQ == null){
			return;
		}
		eventToQueue.forEach((ev,qu) -> {
			//cant delelte the MicroService from the queue if someone is doing round-robin
			synchronized (ev.getName()){
				qu.forEach(ms -> {
					if (ms == m) {
						qu.remove(m);
					}
				});
			}
		});
		broadcastToQueue.forEach((ev,qu) -> qu.forEach(ms-> {
			if(ms == m){
				qu.remove(m);
			}
		}));
		//Sync because without the situation of the queue being remove while other thread (the MicroService thread) poll a message from this queue
		//is optional.
		synchronized (m) {
			serviceToQueue.get(m).forEach(ev -> eventToFuture.get(ev).resolve(null));
			serviceToQueue.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			//We need to be notify somehow, so we wait on m.
			synchronized (m) {
				while (serviceToQueue.get(m).isEmpty()) {
					m.wait();
				}
				return serviceToQueue.get(m).poll();
			}
		}catch (NullPointerException ex1){return null;}
	}

}
