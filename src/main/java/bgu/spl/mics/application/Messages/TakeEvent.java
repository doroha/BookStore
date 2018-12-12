package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeEvent<OrderResult> implements Event<OrderResult> {

    private String bookTitle;

    public TakeEvent(String book){
    this.bookTitle=book;
    }

    public String getBookTitle() {
        return this.bookTitle;
    }
}
