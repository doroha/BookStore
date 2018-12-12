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
	private static ResourcesHolder instance;
	private Vector<DeliveryVehicle> released;
	private Vector<DeliveryVehicle> aquired;


	public static ResourcesHolder getInstance() {
		return singletonHold.resourceInstance;
	}

	private static class singletonHold{
		private static ResourcesHolder resourceInstance= new ResourcesHolder();
	}

	private ResourcesHolder(){
		released=new Vector<>();
		aquired=new Vector<>();
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {  //TODO write this function right so it will return the right object
		if(!released.isEmpty()) {
			Future<DeliveryVehicle> deliveryVehicleFuture;
			//deliveryVehicleFuture= released.get(0);
			//	return deliveryVehicleFuture;
		}
		return null;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(aquired.contains(vehicle)){
			aquired.remove(vehicle);
			released.addElement(vehicle);
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		if (instance != null) {
			for (int i = 0; i < vehicles.length; i++) {
				released.addElement(vehicles[i]);
			}
		}
	}
}