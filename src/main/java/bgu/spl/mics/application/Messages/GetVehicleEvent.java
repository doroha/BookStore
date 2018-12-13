package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class GetVehicleEvent implements Event<DeliveryEvent> {

private DeliveryEvent deliveryEvent;

    public GetVehicleEvent(DeliveryEvent d){
        this.deliveryEvent=d;
    }

    public DeliveryEvent getDeliveryEvent() {
        return this.deliveryEvent;
    }
}
