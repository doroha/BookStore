package bgu.spl.mics.application.Messages;


import bgu.spl.mics.Event;

public class DeliveryEvent<DeliveryVehicle> implements Event<DeliveryVehicle> {

private String adress;
private int distance;


    public DeliveryEvent(int d, String ad) {
        this.adress=ad;
        this.distance=d;
    }
    public String getAdress() {
        return adress;
    }
    public int getDistance() {
        return distance;
    }
}
