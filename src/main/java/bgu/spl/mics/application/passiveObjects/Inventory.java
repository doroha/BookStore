package bgu.spl.mics.application.passiveObjects;

import jdk.nashorn.internal.runtime.OptimisticReturnFilters;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.FileOutputStream;
import java.util.Map;


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

  private List<BookInventoryInfo> booksList;
  private static Inventory instance=null;
  private BookInventoryInfo[] books;

	public static Inventory getInstance() {
        if(instance==null){
            return SingletonH.inventoryNew;
        }
        return instance;
	}
	private static class SingletonH{
	    private static Inventory inventoryNew= new Inventory();
    }
	private Inventory(){
	    books=new BookInventoryInfo[]{};
		booksList=new LinkedList<>();
    }
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */

	public void load (BookInventoryInfo[] inventory ) {
	    for(BookInventoryInfo info :inventory){
	      booksList.add(info);
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
		for (int i = 0; i < booksList.size(); i++) {
			if (booksList.get(i).getBookTitle() == book && booksList.get(i).getAmountInInventory() > 0) {
				booksList.get(i).setAmount(booksList.get(i).getAmountInInventory() - 1);
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
			for(int i= 0 ; i<booksList.size();i++) {
				if (booksList.get(i).getBookTitle() == book && booksList.get(i).getAmountInInventory() > 0) {
					return (booksList.get(i).getPrice());
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
		for(BookInventoryInfo b: books){
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
	}



