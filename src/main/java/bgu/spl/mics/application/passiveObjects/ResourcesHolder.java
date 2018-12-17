package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

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
	private BlockingDeque<DeliveryVehicle> freeVehicle = new LinkedBlockingDeque<>();
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
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> deliveryVehicleFuture = new Future<>();
		if(didTerminate){
			deliveryVehicleFuture.resolve(null);
			return deliveryVehicleFuture;
		}
		deliveryVehicleFuture.resolve(freeVehicle.poll());
		//TODO: I hope that we send reference and we can access the same future object in the queue and in the returned value.
		return deliveryVehicleFuture;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicle.getSpeed() > 0) {//We get a real Vehicle
			freeVehicle.add(vehicle);
		}else {
			didTerminate = true;
			freeVehicle.addFirst(vehicle);
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
