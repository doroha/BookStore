package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */


public class BookInventoryInfo{
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	private int price;
	private int amount;
	private String title;

	public BookInventoryInfo(int price, int amount, String title){
		this.price = price;
		this.amount=amount;
		this.title=title;
	}


	public String getBookTitle() {
		return title;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}
	public void setAmount(int amount){
		this.amount=amount;
	}
	
	

	
}
