package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.*;


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
public class Inventory{

  private Vector<BookInventoryInfo> booksColec;
  private static Inventory instance=null;

	public static Inventory getInstance() {
            return SingletonH.inventoryNew;
	}
	private static class SingletonH{
	    private static Inventory inventoryNew= new Inventory();
    }
	private Inventory(){

    }
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */

	public void load (BookInventoryInfo[] inventory ) {
		if (instance!=null) {
			for (int i = 0; i < inventory.length; i++) {
				booksColec.addElement(inventory[i]);
			}
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
	public OrderResult take (String book) {
		for (BookInventoryInfo b:booksColec) {
			if (b.getBookTitle().equals(book) && b.getAmountInInventory() > 0) {
				b.setAmount(b.getAmountInInventory() - 1);
				return OrderResult.valueOf("SUCCESSFULLY_TAKEN");
			}
		}
		return OrderResult.valueOf("NOT_IN STOCK");
	}
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		for (BookInventoryInfo b : booksColec) {
			if (b.getBookTitle().equals(book)) {
				return b.getPrice();
			}
		}
		return -1;
	}

	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile (String filename){
		HashMap<String, Integer> hashBook=new HashMap<>();
		for(BookInventoryInfo b: booksColec){
			hashBook.put(b.getBookTitle(),b.getAmountInInventory());
		}
		try {
			File file = new File("outExample.txt");
			FileOutputStream outputF = new FileOutputStream(file);
			PrintWriter printWrite = new PrintWriter(outputF);
			for (Map.Entry<String, Integer> m : hashBook.entrySet()) {
				printWrite.println(m.getKey() + ", amount: " + m.getValue());
			}
			printWrite.flush();
			printWrite.close();
			outputF.close();
		}catch (Exception e){}
		}

		public int amountBooks(){return booksColec.size();}

	}