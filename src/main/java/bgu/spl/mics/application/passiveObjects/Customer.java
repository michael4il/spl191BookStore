package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {
	private int id;
	private String name;
	private String address;
	private int Distance;
	private List<OrderReceipt> list;
	private int creditCard;
	private int availableAmountInCreditCard;



	public Customer (int id,String name,String address,int Distance ,int ccard,int csum){
		this.id = id;
		this.name = name;
		this.address=address;
		this.Distance=Distance;
		creditCard=ccard;
		availableAmountInCreditCard=csum;

	}
	public String getName() { return name; }
	public int getId() { return id; }
	public String getAddress() { return address; }
	public int getDistance() { return Distance; }
	public List<OrderReceipt> getCustomerReceiptList() { return list; }
	public int getAvailableCreditAmount() { return availableAmountInCreditCard; }      // GETS
	public int getCreditNumber() { return creditCard; }
	
}
