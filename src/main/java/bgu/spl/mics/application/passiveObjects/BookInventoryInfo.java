package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private String bookTitle;
	private int currentAmount;
	private int price;

	public BookInventoryInfo(String title, int amount, int newprice){
		bookTitle = title;
		currentAmount = amount;
		price = newprice;
	}


	public String getBookTitle() { return bookTitle; }
	public int getAmountInInventory() { return currentAmount; }
	public int getPrice() { return price; }

	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}
}
