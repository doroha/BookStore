package bgu.spl.mics.application.passiveObjects;

import javafx.util.Pair;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private String name;
	private int Id;
	private String address;
	private int distance;
	private int creditNum;
	private int amount;
	private List<OrderReceipt> receipts;
	public Customer (String name, int Id, String address, int distance,
					 int creditNum, int amount ){
		this.name= name;
		this.Id=Id;
		this.address=address;
		this.distance=distance;
		this.creditNum=creditNum;
		this.amount=amount;
		this.receipts=new LinkedList<>();
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return Id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return amount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditNum;
	}

	public void chargeCreditCard(int price){
		this.amount=this.amount - price;
	}

	public boolean possibleCharge(int price){return getAvailableCreditAmount()>=price;}

	//add new recipt
	public void file(OrderReceipt receipt){this.receipts.add(receipt);}

	@Override
	public String toString() {
		return "Customer{" +
				"name='" + name + '\'' +
				", Id=" + Id +
				", address='" + address + '\'' +
				", distance=" + distance +
				", creditNum=" + creditNum +
				", amount=" + amount +
				", receipts=" + receipts +
				'}';
	}
}