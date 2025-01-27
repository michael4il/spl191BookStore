package bgu.spl.mics.application.services;

import bgu.spl.mics.Messages.Broadcasts.AcquireBookEvent;
import bgu.spl.mics.Messages.Broadcasts.CheckAvailabiltyAndGetPriceEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

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
	private Inventory Inv = Inventory.getInstance();
	private int currectTick;
	private CountDownLatch countDownLatch;

	public InventoryService(int i ,CountDownLatch countDownLatch) {
		super("InventoryService "+i);
		this.countDownLatch = countDownLatch;
	}
	@SuppressWarnings("Duplicates")
	@Override
	protected synchronized  void  initialize() {
		//************************************************TIME******************************
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			if(message.getLast()) {
				terminate();
			}
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
		countDownLatch.countDown();

	}

}
