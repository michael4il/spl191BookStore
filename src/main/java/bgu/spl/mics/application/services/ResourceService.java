package bgu.spl.mics.application.services;

import bgu.spl.mics.Messages.Broadcasts.AskForVehicle;
import bgu.spl.mics.Messages.Broadcasts.ReleaseVehicleEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private CountDownLatch countDownLatch;

	private int currectTick = 0;

	public ResourceService(int i, CountDownLatch countDownLatch) {
		super("ResourceService "+i);
		this.countDownLatch = countDownLatch;

	}
	@SuppressWarnings("Duplicates")
	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class, message -> {
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
			if(message.getLast()) {
				ResourcesHolder.getInstance().releaseVehicle(new DeliveryVehicle(0,-1));//An imaginary vehicle that marks that we should release all vehicles in ResourcesHolder
				terminate();
			}
		});
		subscribeEvent(AskForVehicle.class, message -> {
			DeliveryVehicle currentVehicle = ResourcesHolder.getInstance().acquireVehicle().get();
			complete(message, currentVehicle );
		});
		subscribeEvent(ReleaseVehicleEvent.class, message -> {
			ResourcesHolder.getInstance().releaseVehicle(message.getDeliveryVehicle());
		});
		countDownLatch.countDown();


	}

}
