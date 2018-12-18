package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	ConcurrentLinkedDeque<OrderReceipt> orderReceiptList=new ConcurrentLinkedDeque<>();

	private  static class SingletonHolder{
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {

		return SingletonHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orderReceiptList.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int sum=0;
		for(OrderReceipt orderReceipt : orderReceiptList)
			sum=sum+orderReceipt.getPrice();
		return sum;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAvailableAmountInCreditCard(c.getAvailableCreditAmount()-amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.
     */@SuppressWarnings("Duplicates")
	public void printOrderReceipts(String filename) {
		List<OrderReceipt> list = new LinkedList<>();
		for(OrderReceipt orderReceipt: orderReceiptList)
			list.add(orderReceipt);
		try
		{
			FileOutputStream fileStream = new FileOutputStream(filename);
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(list);
			objectStream.close();
			fileStream.close();

		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
