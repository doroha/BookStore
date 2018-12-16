package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private CountDownLatch latch;

	public LogisticsService(int number,CountDownLatch lat) {
		super("LogisticService "+ number);
		this.latch=lat;
	}

	@Override
	protected void initialize() {

		System.out.println(getName()+ " Hello Book Store");
		subscribeEvent(DeliveryEvent.class, (DeliveryEvent d) -> {

			System.out.println(getName()+ " get DeliveryEvent and send GetVehicleEvent");
			Future<Future<DeliveryVehicle>> futureVehicle = (Future<Future<DeliveryVehicle>>) sendEvent(new GetVehicleEvent(d));
			if (futureVehicle!=null) {
				DeliveryVehicle vehicle;
				Future<DeliveryVehicle> future = futureVehicle.get();
				vehicle = future.get();
				System.out.println(getName() + " get Vehicle");
				if (vehicle != null) {
					System.out.println(getName() + " send deliver with: " + vehicle.getLicense());
					vehicle.deliver(d.getAdress(), d.getDistance());   //the vehicle is sleep now fot the time of the deliver.
					System.out.println("The Deliver is done");
					complete(d, vehicle);
					sendEvent(new ReturenVehicleEvent(vehicle));  //send the free vehicle after his deliver back to the resorceHolder that he will keep it and relase new request
				} else {
					System.out.println("future-Null");
					complete(d, null);
				}
			}	else {
				System.out.println("future-Null");
				complete(d, null);
			}
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick)->{
			terminate();
		});
		latch.countDown();
	}
}
