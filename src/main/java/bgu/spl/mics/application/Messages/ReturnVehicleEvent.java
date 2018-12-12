package bgu.spl.mics.application.Messages;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReturnVehicleEvent {

    DeliveryVehicle veh;
    public ReturnVehicleEvent(DeliveryVehicle veh){
    this.veh=veh;
    }
    public DeliveryVehicle getVehicle(){return veh;}
}
