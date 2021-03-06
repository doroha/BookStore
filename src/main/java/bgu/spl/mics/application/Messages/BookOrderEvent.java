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
    private int orderTick;

public BookOrderEvent(Customer c,String b,int tick){
    this.customer=c;
    this.book=b;
    this.orderTick=tick;
}
    public String getBookTitle() {
        return book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick(){return this.orderTick;}
}
