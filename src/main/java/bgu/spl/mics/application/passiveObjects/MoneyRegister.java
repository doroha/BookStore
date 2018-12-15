package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.*;

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

	private List<OrderReceipt> receipts;
	private static MoneyRegister instance;
	private int total_Earnings;

	private static class SingletonH{
		private static MoneyRegister moneyNew= new MoneyRegister();
	}

	public static MoneyRegister getInstance() {
return SingletonH.moneyNew;
	}

	private MoneyRegister(){
		receipts=new LinkedList<>();
		total_Earnings=0;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		receipts.add(r);
	}
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return total_Earnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
			c.chargeCreditCard(amount);
			total_Earnings+=amount;
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output..
     */

		public void printOrderReceipts(String filename) {
			try {
				FileOutputStream outputF = new FileOutputStream(filename);
				ObjectOutputStream outputStream=new ObjectOutputStream(outputF);
				outputStream.writeObject(receipts);
				outputStream.close();
				outputF.close();
			}catch (IOException I){I.printStackTrace(); }
		}
}
