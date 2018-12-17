package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.Broadcasts.BookOrderEvent;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer me;
	private int currectTick = 1;
    private CountDownLatch countDownLatch;


	List<Future<OrderReceipt>> futureList= new LinkedList<>();
	public APIService(Customer customer, CountDownLatch countDownLatch) {
		super("WebAPI of  "+customer.getName());
		this.me=customer;
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected  void initialize() {
		subscribeBroadcast(Tick.class, message->{
			currectTick=message.getTickNumber();
			System.out.println(getName() +"  time : "+currectTick);
			if(message.getLast()) {
				terminate();
			}else{
				while(!me.getOrderlist().isEmpty() && me.getOrderlist().get(0).getValue()==currectTick)
				{
					OrderReceipt receipt=new OrderReceipt(me.getOrderlist().get(0).getValue(),me.getOrderlist().get(0).getKey(),me.getId());//
					Future<OrderReceipt>future = sendEvent(new BookOrderEvent(me,receipt));
					me.getOrderlist().remove(0);
					futureList.add(future);
				}

				while(!futureList.isEmpty()){
					Future<OrderReceipt> receiptFuture = futureList.get(0);
					OrderReceipt orderReceipt = receiptFuture.get();

					if(orderReceipt != null) {
						System.out.println(orderReceipt);
						me.getCustomerReceiptList().add(orderReceipt);
						System.out.println("Customer has  "+me.getAvailableCreditAmount());
					}
					futureList.remove(0);
				}
//			*****************************************************DELIVERY  EVENT********************************************
				// make a list of futures?

//					if(future.isDone()) {
//						OrderReceipt receipt = future.get();
//						if(receipt!=null) {
//							sendEvent(new DeliveryEvent(customer));

			}
		});
		countDownLatch.countDown();
	}
}