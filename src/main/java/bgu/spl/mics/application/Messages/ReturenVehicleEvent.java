package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReturenVehicleEvent implements Event<DeliveryVehicle> {

    private DeliveryVehicle freeVehicle;
    public ReturenVehicleEvent(DeliveryVehicle vhicle){
    this.freeVehicle=vhicle;
    }
    public DeliveryVehicle getFreeVehicle(){
        return this.freeVehicle;
    }
}
