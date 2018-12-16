package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private ConcurrentLinkedQueue<DeliveryVehicle> freeVehicle = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> waitingFutures = new ConcurrentLinkedQueue<>();
	private boolean didTerminate = false;
	private static class SingletonHolder{
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> deliveryVehicleFuture = new Future<>();
		if(didTerminate){
			deliveryVehicleFuture.resolve(null);
			return deliveryVehicleFuture;
		}
		if(!freeVehicle.isEmpty()){
			deliveryVehicleFuture.resolve(freeVehicle.poll());
		}
		else{
			waitingFutures.add(deliveryVehicleFuture);
		}
		//TODO: I hope that we send reference and we can access the same future object in the queue and in the returned value.
		return deliveryVehicleFuture;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicle.getSpeed() > 0) {//We get a real Vehicle
			if (!waitingFutures.isEmpty()) {
				waitingFutures.poll().resolve(vehicle);
			} else {
				freeVehicle.add(vehicle);
			}
		}else {
			didTerminate = true;
			waitingFutures.forEach(fut -> {
				fut.resolve(null);
			});
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	//Not Sync
	public void load(DeliveryVehicle[] vehicles) {
		for(DeliveryVehicle delVehicle : vehicles){
			freeVehicle.add(delVehicle);
		}
	}

}
