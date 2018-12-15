package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.*;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private Queue<DeliveryVehicle> freeVehicles; // Queue for the free vehicles.
	private Queue<Future<DeliveryVehicle>> requestVehicles; // Queue for the futures for us that we know if there request to be processed.

	public static ResourcesHolder getInstance() {
		return singletonHold.resourceInstance;
	}

	private static class singletonHold{
		private static ResourcesHolder resourceInstance= new ResourcesHolder();
	}

	private ResourcesHolder(){
		freeVehicles=new LinkedList<>();
		requestVehicles=new LinkedList<>();
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {  //TODO write this function right so it will return the right object
		Future<DeliveryVehicle> future=new Future<>();
		if (freeVehicles.isEmpty()){  //if there is no release vehicle
			requestVehicles.add(future); //add request for vhicle by setting future with null value that when we get free vhicle we resolve it.

		} else {  //there is free vhicle
			future.resolve(freeVehicles.poll());
		}
		return future;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if (!requestVehicles.isEmpty()) { //there is some request for delivery so we release this request with The released vehicle.
			requestVehicles.poll().resolve(vehicle);
		}else { //there is no request that waitings for and we add this vhicle to the Queue of the free vhicle
			freeVehicles.add(vehicle);
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle delivery:vehicles){
			freeVehicles.add(delivery);
		}
	}
}