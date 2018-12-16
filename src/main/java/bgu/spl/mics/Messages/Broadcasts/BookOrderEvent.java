package bgu.spl.mics.Messages.Broadcasts;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event {
    private Customer customer;
    private OrderReceipt orderReceipt;

    /**
     * lastTick means that in the last tick we will use different get
     *
     */
    //we own customer for locking
    //receipt has now book and order tick ,need to updated and return in the future


    public BookOrderEvent (Customer c,OrderReceipt o)
    {
        customer=c;
        orderReceipt=o;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }
}
