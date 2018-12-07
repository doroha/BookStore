package bgu.spl.mics.application.Messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;

public class BookOrderEvent<T> implements Event<T> {

ConcurrentHashMap<OrderReceipt,Customer> orders;
private Inventory inventory;

public BookOrderEvent(){

    inventory=Inventory.getInstance();
    orders=new ConcurrentHashMap<>();
}

public boolean processing(String book){
if (inventory.isAvailable(book)) { }

    return true;
}
}
