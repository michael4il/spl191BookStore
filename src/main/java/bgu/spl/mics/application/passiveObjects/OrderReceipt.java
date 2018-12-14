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
	@Override
	public String toString() {
		return "-----------------ORDER TOSTRING-------------\nOrder Receipt = " + orderID + "\nSellerID = "+seller+"\ncustomerID = "+ customerId+"\nbookTitle = "+bookTitle+
				"\nPrice = "+price+"\nissueTick = "+issueTick+"\norderTick = "+orderTick+"\nprocessTick = "+processTick+"\n---------------order end-----------------";
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

	public int getIssueTick() {
		return issueTick;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setIssueTick(int issueTick) {
		this.issueTick = issueTick;
	}

	public void setOrderTick(int orderTick) {
		this.orderTick = orderTick;
	}

	public void setProcessTick(int processTick) {
		this.processTick = processTick;
	}


}
