package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
	private static ResourcesHolder instance=null;
	private Vector<DeliveryVehicle> vehiclesColec;

	public static ResourcesHolder getInstance() {
		if(instance==null){
			return singletonHold.resource;
		}
		return instance;
	}
	private static class singletonHold{
		private static ResourcesHolder resource= new ResourcesHolder();
	}
	private ResourcesHolder(){
		vehiclesColec=new Vector<>();
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {  //TODO
		for (DeliveryVehicle vehicle:vehiclesColec) {
			if (vehicle.isOpen()) {
				Future<DeliveryVehicle>future=new Future<>();

			}
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
		vehicle.openVehicle();
	}

	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
    Collections.addAll(vehiclesColec, vehicles);
	}
}
