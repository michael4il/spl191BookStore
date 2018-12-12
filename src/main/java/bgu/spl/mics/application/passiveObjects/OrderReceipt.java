package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customerId after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt {
	private int orderID;
	private String seller;
	private int customerId;
	private String bookTitle;
	private int price;
	private int issueTick;
	private int orderTick;
	private int processTick;
	public OrderReceipt(int orderTick,String bookTitle,int customerid)
	{
		this.bookTitle=bookTitle;
		this.orderTick=orderTick;
		this.customerId =customerid;

	}

	//***************************GETS****************************
	public int getOrderId() { return orderID; }
	public String getSeller() { return seller; }
	public int getCustomerId() { return customerId; }
	public String getBookTitle() { return bookTitle; }
	public int getPrice() { return price; }
	public int getIssuedTick() { return issueTick; }
	public int getOrderTick() { return orderTick; }
	public int getProcessTick() { return processTick; }
}
