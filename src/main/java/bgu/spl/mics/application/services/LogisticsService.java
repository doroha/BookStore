package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

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



	public LogisticsService(int number) {
		super("LogisticService "+ number);

	}

	@Override
	protected void initialize() {


		subscribeEvent(DeliveryEvent.class, (DeliveryEvent d) -> {

			Future<Future<DeliveryVehicle>> futureVehicle = (Future<Future<DeliveryVehicle>>) sendEvent(new GetVehicleEvent(d));
			DeliveryVehicle veh;
			if (futureVehicle!=null) {
				 veh =futureVehicle.get().get();
				if(veh !=null){
					DeliveryEvent delivery =new DeliveryEvent(d.getDistance(),d.getAddress());
					Future<DeliveryVehicle> future= (Future<DeliveryVehicle>) sendEvent(new ReturnVehicleEvent(veh));
				}
			}
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick)->{
			terminate();
		});
	}

}
