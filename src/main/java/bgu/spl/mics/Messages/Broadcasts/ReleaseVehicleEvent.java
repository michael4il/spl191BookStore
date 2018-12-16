package bgu.spl.mics.Messages.Broadcasts;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    private DeliveryVehicle deliveryVehicle;
    public ReleaseVehicleEvent(DeliveryVehicle deliveryVehicle){
        this.deliveryVehicle = deliveryVehicle;
    }

    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }
}
