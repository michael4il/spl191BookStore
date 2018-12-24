package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.Broadcasts.AskForVehicle;
import bgu.spl.mics.Messages.Broadcasts.DeliveryEvent;
import bgu.spl.mics.Messages.Broadcasts.ReleaseVehicleEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch;

	private int currectTick = 0;
	public LogisticsService(int i, CountDownLatch countDownLatch) {
		super("LogisticsService " + i);
		this.countDownLatch = countDownLatch;

	}

	@SuppressWarnings("Duplicates")
	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			if(message.getLast()) {
				terminate();
			}
		});
		subscribeEvent(DeliveryEvent.class, message -> {
			Future<DeliveryVehicle> futureVehicleThatWillDeliver = sendEvent(new AskForVehicle());
			try {
				DeliveryVehicle vehicleThatWillDeliver = futureVehicleThatWillDeliver.get();//waiting for car
				vehicleThatWillDeliver.deliver(message.getCustomer().getAddress(),message.getCustomer().getDistance() ); //car goes vroom vroom
				sendEvent(new ReleaseVehicleEvent(vehicleThatWillDeliver));// return vehicle
			}
			//Can be nullpointer because the last Tick make it null, and the sleep may throw interrupted exception.
			catch (Exception e){}
		});
		countDownLatch.countDown();

	}

}
