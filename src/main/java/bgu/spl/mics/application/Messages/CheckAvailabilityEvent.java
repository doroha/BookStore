package bgu.spl.mics.application.Messages;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.Event;

public class CheckAvailabilityEvent<Integer> implements Event<Integer> {


    private String book;
    public CheckAvailabilityEvent(String b){
        this.book=b;
    }

    public String getBookTitle(){
        return this.book;
    }
}
