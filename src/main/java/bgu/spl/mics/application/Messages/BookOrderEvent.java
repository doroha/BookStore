package bgu.spl.mics.application.Messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


public class BookOrderEvent<OrderReceipt> implements Event<OrderReceipt> {

    private Customer customer;
    private String book;
    private int orderId;  //TODO

public BookOrderEvent(Customer c,String b){
    this.customer=c;
    this.book=b;
}
    public String getBook() {
        return book;
    }

    public Customer getCustomer() {
        return customer;
    }
}
