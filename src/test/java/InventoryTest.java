package java;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Test;

import java.awt.print.Book;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory invent = new Inventory();

    @Test
    public void getInstance() {
        assertEquals(null, invent.getInstance());
    }

    @Test
    public void load() {
        BookInventoryInfo books[];
        books = new BookInventoryInfo[0];
        invent.load(books);
        assertEquals(invent.checkAvailabiltyAndGetPrice("Harry Potter"), -1);
    }

    @Test
    public void BOOK_take() { //returns there is no book called Harry Potter
        BookInventoryInfo books[];
        books = new BookInventoryInfo[1];
        books[0] = new BookInventoryInfo(20, 5, "Harry Potter");
        assertEquals(OrderResult.valueOf("SUCCESSFULLY_TAKEN"), invent.take("Harry Potter"));
        assertEquals(OrderResult.valueOf("NOT_IN_STOCK"), invent.take("Shreck"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        assertEquals(-1, invent.checkAvailabiltyAndGetPrice("Harry Potter"));
    }
}