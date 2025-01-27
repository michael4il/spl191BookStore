package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
	private ConcurrentHashMap<String, BookInventoryInfo> bookNametoInfo = new ConcurrentHashMap<>();
	private static class SingletonHolder{
		private static Inventory instance = new Inventory();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	//Not sync
	public void load (BookInventoryInfo[ ] inventory ) {
		for (BookInventoryInfo bookInfo : inventory)
		{
			bookNametoInfo.put(bookInfo.getBookTitle(),bookInfo);
		}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	//sync on the book
	public OrderResult take (String book) {
		synchronized (book)
		{
			BookInventoryInfo bookInfo = bookNametoInfo.get(book);
			if(bookInfo.getAmountInInventory()>=1){
				bookInfo.setCurrentAmount(bookInfo.getAmountInInventory()-1);
				//System.out.println(bookInfo.getBookTitle()+ " Current Amount: "+ bookInfo.getAmountInInventory());
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
			else
			{
				return OrderResult.NOT_IN_STOCK;
			}
		}
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	//Do not have to be sync because it will check later if it is available when it will be sync on the book.
	//This check is only for pre check and for the customer it is THE check if he/she has enough money.
	public int checkAvailabiltyAndGetPrice(String book) {
		if(bookNametoInfo.get(book)!=null)
		{
			return bookNametoInfo.get(book).getPrice();
		}
		return -1;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a
	 * Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */@SuppressWarnings("Duplicates")
	public void printInventoryToFile(String filename){//make new map for output
		HashMap<String, Integer> hashmap = new HashMap<>();
		bookNametoInfo.forEach((book,b)->hashmap.put(b.getBookTitle(),b.getAmountInInventory()));
		try
		{
			FileOutputStream fileStream = new FileOutputStream(filename);
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(hashmap);
			objectStream.close();
			fileStream.close();

		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

	}
}
