package bgu.spl.mics.application.services;

import bgu.spl.mics.Messages.Broadcasts.AcquireBookEvent;
import bgu.spl.mics.Messages.Broadcasts.CheckAvailabiltyAndGetPriceEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	Inventory Inv = Inventory.getInstance();
	int currectTick;

	public InventoryService(int i) {
		super("InventoryService "+i);
	}

	@Override
	protected void initialize() {
		//************************************************TIME******************************
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
		});
		//***********************************************Subscribe CheckAvailability**************************
		subscribeEvent(CheckAvailabiltyAndGetPriceEvent.class, (message)->{
			String book= message.getReceipt().getBookTitle();
			int price=Inventory.getInstance().checkAvailabiltyAndGetPrice(book);
			complete(message,price);
		});
		//**********************************************SUBSCRIBE ACQUIRE****************************************************
		subscribeEvent(AcquireBookEvent.class, (message)->{
			OrderResult orderResult=Inv.take(message.getOrderReceipt().getBookTitle());
			complete(message,orderResult);
		});
	}

}
