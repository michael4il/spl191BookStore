package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt {
	private int orderID;
	private String seller;
	private int customer;
	private String bookTitle;
	private int price;
	private int issueTick;
	private int orderTick;
	private int processTick;
	

	public int getOrderId() { return orderID; }//***************************GETS****************************
	public String getSeller() { return seller; }
	public int getCustomerId() { return customer; }
	public String getBookTitle() { return bookTitle; }
	public int getPrice() { return price; }
	public int getIssuedTick() { return issueTick; }
	public int getOrderTick() { return orderTick; }
	public int getProcessTick() { return processTick; }
}
