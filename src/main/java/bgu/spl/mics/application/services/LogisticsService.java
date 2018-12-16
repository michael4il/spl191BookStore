package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.Broadcasts.AskForVehicle;
import bgu.spl.mics.Messages.Broadcasts.DeliveryEvent;
import bgu.spl.mics.Messages.Broadcasts.ReleaseVehicleEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

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

	private int currectTick = 0;
	public LogisticsService(int i) {
		super("LogisticsService " + i);
	}

	@SuppressWarnings("Duplicates")
	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
			if(message.getLast()) {
				terminate();
			}
		});
		subscribeEvent(DeliveryEvent.class, message -> {
			System.out.println("Received DeliveryEvent with: " + message.getCustomer().getName());
			Future<DeliveryVehicle> futureVehicleThatWillDeliver = sendEvent(new AskForVehicle());
			try {
				DeliveryVehicle vehicleThatWillDeliver = futureVehicleThatWillDeliver.get();
				vehicleThatWillDeliver.deliver(message.getCustomer().getAddress(),message.getCustomer().getDistance() );
				sendEvent(new ReleaseVehicleEvent(vehicleThatWillDeliver));
			}
			//Can be nullpointer because the last Tick make it null, and the sleep may throw interrupted exception.
			catch (Exception e){}
		});
	}

}
