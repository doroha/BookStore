package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.GetVehicleEvent;
import bgu.spl.mics.application.Messages.ReturenVehicleEvent;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

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
	private CountDownLatch latch;
	private Vector<Future<DeliveryVehicle>> futureVector;

	public ResourceService(int number,CountDownLatch lat) {
		super("Resource Service " + number);
		this.holder = ResourcesHolder.getInstance();
		this.latch=lat;
		this.futureVector=new Vector<>();
	}

	@Override
	protected void initialize() {

		System.out.println(getName()+ " Hello Book Store");

		subscribeEvent(GetVehicleEvent.class, (GetVehicleEvent v) -> {
			System.out.println(getName()+ " Tries to acquire Vehicle");
			Future<DeliveryVehicle> deliveryVehicleFuture=holder.acquireVehicle();
			futureVector.add(deliveryVehicleFuture);
			complete(v,deliveryVehicleFuture);
		});

		subscribeEvent(ReturenVehicleEvent.class,(ReturenVehicleEvent rt)->{  // free the vehicle after his delivery
			DeliveryVehicle freeVehicle=rt.getFreeVehicle();
			holder.releaseVehicle(freeVehicle);
			complete(rt,freeVehicle);
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick)->{
			//TODO - stop all the vhicle threads and put in all thier future- null .
			for (Future<DeliveryVehicle> future:futureVector){
				if (!future.isDone()) {
					future.resolve(null);
				}
				System.out.println("Vehicle dead");
			}
			terminate();
		});
		latch.countDown();
	}
}


