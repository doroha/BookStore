package bgu.spl.mics.application.passiveObjects;

import jdk.nashorn.internal.runtime.OptimisticReturnFilters;

import java.util.HashMap;
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

  private  Map <String, Integer > inventory;

    private static Inventory instance=null;
    private BookInventoryInfo[] books;

	/**
     * Retrieves the single instance of this class.
     */
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
	      //  inventory.put(info.getBookTitle(),info);
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
	  //  if((inventory.containsKey(book))&& (inventory.get(book).getAmountInInventory()>0))
        {
           // inventory.get(book).setAmount(inventory.get(book).getAmountInInventory()-1);
            return OrderResult.valueOf("SUCCESSFULLY_TAKEN");
        }
		//else
		   // return OrderResult.valueOf("NOT_IN STOCK");
	}

	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
      //  if((inventory.containsKey(book))&& (inventory.get(book).getAmountInInventory()>0)){


           // return (inventory.get(book).getPrice());
        //}
	//	else {
            return -1;
        }
	//}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		//TODO: Implement this
	}
}
