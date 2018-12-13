package bgu.spl.mics.application.Messages;


import bgu.spl.mics.Event;

public class DeliveryEvent<DeliveryVehicle> implements Event<DeliveryVehicle> {

private String adress;
private String book;


    public DeliveryEvent(String bookTitle, String address) {
        this.adress=bookTitle;
        this.book=address;
    }

    public String getBook() {
        return this.book;
    }
    public String getAdress() {
        return this.adress;
    }
}
