package bgu.spl.mics.application.Messages;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.Event;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import java.awt.*;

public class CheckAvailabilityEvent<BookInventoryInfo> implements Event<BookInventoryInfo> {

    private String book;
    public CheckAvailabilityEvent(String b){
        this.book=b;
    }

    public String getBook(){
        return this.book;
    }
}
