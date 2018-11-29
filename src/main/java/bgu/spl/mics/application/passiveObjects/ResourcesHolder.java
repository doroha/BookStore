package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
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
	private  List<DeliveryVehicle > vehicleList;

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		if(instance==null){
			return singletonHold.resource;
		}
		return instance;
	}
	private static class singletonHold{
		private static ResourcesHolder resource= new ResourcesHolder();
	}
	private void Resource(){
		vehicleList=new LinkedList<>();
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		//TODO: Implement this
		return null;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicleList.contains(vehicle)){
			vehicleList.remove(vehicle);
		}


	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(DeliveryVehicle v : vehicles){
			vehicleList.add(v);
		}
	}

}
