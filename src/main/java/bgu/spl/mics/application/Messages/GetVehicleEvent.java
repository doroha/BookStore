package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class GetVehicleEvent<DeliveryVehicle> implements Event<DeliveryVehicle> {

    private String adress;
    private int distance;


    public GetVehicleEvent(DeliveryEvent d){
    this.adress=d.getAdress();
    this.distance=d.getDistance();
    }
    public String getAdress() {
        return this.adress;
    }
    public int getDistance() {
        return this.distance;
    }
}
