package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.GetVehicleEvent;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {

	private ResourcesHolder holder;

	public ResourceService(int number) {
		super("Resource Service " + number);
		this.holder = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {

		subscribeEvent(GetVehicleEvent.class, (GetVehicleEvent v) -> {
			System.out.println(getName()+ " Hello Book Store");
			DeliveryVehicle vehicle=holder.acquireVehicle().get();
			complete(v,vehicle);
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick)->{
			//TODO - stop all the vhicle threads.
			terminate();
		});
	}
}


