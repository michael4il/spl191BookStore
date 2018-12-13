package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.Broadcasts.AcquireBookEvent;
import bgu.spl.mics.Messages.Broadcasts.BookOrderEvent;
import bgu.spl.mics.Messages.Broadcasts.CheckAvailabiltyAndGetPriceEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

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
	private int currectTick;
	public SellingService(int num) {
		super("SellingService"+num);
		moneyRegister=MoneyRegister.getInstance();
		currectTick=0;

	}

	@Override
	protected void initialize() {
//************************************************TIME******************************
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
	});




		//********************************************BOOK EVENT SUBSCRIBE**************************************************************************
		subscribeEvent(BookOrderEvent.class,message->{
			Customer customer= message.getCustomer();
			OrderReceipt orderDetails = message.getOrderReceipt();
			orderDetails.setProcessTick(currectTick);


			Future<Integer> bookPriceFuture= sendEvent(new CheckAvailabiltyAndGetPriceEvent(orderDetails));//MUST CHECK! why not sending event


			int price=bookPriceFuture.get();//here comes the waiting
			if(price != -1 && customer.getAvailableCreditAmount()>=price)
			{

				//locking happens here because of MoneyRegister.chargeCreditCard
				synchronized (customer) {
					Future<OrderResult> orderResultFuture = sendEvent(new AcquireBookEvent(orderDetails, customer));// need to update receipt price
					OrderResult orderResult = orderResultFuture.get();//waiting here order Details.setSeller(this.getName());
					moneyRegister.chargeCreditCard(customer,price);
				}


			}

			{


				orderDetails.setIssueTick(currectTick);






			}


		});

}}
