package bgu.spl.mics.Messages.Broadcasts;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class AcquireBookEvent implements Event{
    OrderReceipt orderReceipt;
    Customer customer;
    public AcquireBookEvent(OrderReceipt orderReceipt, Customer customer) {
        this.customer=customer;
        this.orderReceipt=orderReceipt;}

    public OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }
}
