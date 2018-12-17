package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.Broadcasts.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private CountDownLatch countDownLatch;

	private int currectTick;
	public SellingService(int num, CountDownLatch countDownLatch) {
		super("SellingService"+num);
		moneyRegister=MoneyRegister.getInstance();
		currectTick=0;
		this.countDownLatch = countDownLatch;


	}

	@Override
	protected void initialize() {
//************************************************TIME******************************
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
			if(message.getLast()) {
				terminate();
			}
	});




		//********************************************BOOK EVENT SUBSCRIBE**************************************************************************
		subscribeEvent(BookOrderEvent.class,message->{
			Customer customer= message.getCustomer();//some reference
			OrderReceipt orderDetails = message.getOrderReceipt();
			orderDetails.setProcessTick(currectTick);//order update


			Future<Integer> bookPriceFuture = sendEvent(new CheckAvailabiltyAndGetPriceEvent(orderDetails));
			int price = bookPriceFuture.get();//here comes the waiting
			if(price != -1 && customer.getAvailableCreditAmount()>=price)
			{

				//locking happens here because of MoneyRegister.chargeCreditCard is here
				synchronized (customer) {
					Future<OrderResult> orderResultFuture = sendEvent(new AcquireBookEvent(orderDetails, customer));

					OrderResult orderResult = orderResultFuture.get();//waiting here,need to prevent deadlock in final tick and no logistics services available

					if(orderResult==OrderResult.SUCCESSFULLY_TAKEN&&customer.getAvailableCreditAmount()>=price)
					{
						System.out.println("Book SUCCESSFULLY_TAKEN");
						moneyRegister.chargeCreditCard(customer,price);
						//some order updates
						orderDetails.setSeller(this.getName());
						orderDetails.setIssueTick(currectTick);
						orderDetails.setPrice(price);

						MoneyRegister.getInstance().file(orderDetails);//we need to make new receipt?
						complete(message,orderDetails);
						System.out.println("Sending Delivery Event to customer: " +customer.getName());
						sendEvent(new DeliveryEvent(customer));

					}
					else{
						System.out.println("Book Denied error *2fast4u*");
						complete(message,null);}
				}
			}
			complete(message,null);//falls in check availability

		});
		countDownLatch.countDown();

	}
}
