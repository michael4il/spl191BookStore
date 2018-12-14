package bgu.spl.mics.application.passiveObjects;

import javafx.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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
	private List<OrderReceipt> list= new LinkedList();
	private int creditCard;
	private int availableAmountInCreditCard;
	private List<Pair<String, Integer>> orderlist;// increasing order



	public Customer (int id,String name,String address,int Distance ,int ccard,int csum,List<Pair<String, Integer>> orderlist){
		this.id = id;
		this.name = name;
		this.address=address;
		this.Distance=Distance;
		creditCard=ccard;
		availableAmountInCreditCard=csum;
		this.orderlist=orderlist;
		Collections.sort(orderlist, new Comparator<Pair<String, Integer>>() {
			@Override
			public int compare(final Pair<String, Integer> o1, final Pair<String, Integer> o2) {
				return o1.getValue()-o2.getValue();
			}
		});

	}
	public List<Pair<String, Integer>> getOrderlist() {return orderlist;}
	public String getName() { return name; }
	public int getId() { return id; }
	public String getAddress() { return address; }
	public int getDistance() { return Distance; }
	public List<OrderReceipt> getCustomerReceiptList() { return list; }
	public int getAvailableCreditAmount() { return availableAmountInCreditCard; }      // GETS
	public int getCreditNumber() { return creditCard; }

	public void setAvailableAmountInCreditCard(int availableAmountInCreditCard) {
		this.availableAmountInCreditCard = availableAmountInCreditCard;
	}
}
