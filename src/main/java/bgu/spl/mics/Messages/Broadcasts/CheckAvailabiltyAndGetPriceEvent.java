package bgu.spl.mics.Messages.Broadcasts;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class CheckAvailabiltyAndGetPriceEvent implements Event{
    OrderReceipt receipt;
    public CheckAvailabiltyAndGetPriceEvent(OrderReceipt receipt)
    {
        this.receipt=receipt;
    }

    public OrderReceipt getReceipt() {
        return receipt;
    }
}
