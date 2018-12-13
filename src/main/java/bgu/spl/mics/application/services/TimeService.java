package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.TickBroadcast;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{


	private int currentTick;
	private int speedTime;
	private int durationTime;
	private static TimeService instance=null;

	private TimeService(int speed,int duration) {
		super("Time Service");
		this.speedTime=speed;
		this.durationTime=duration;
		this.currentTick=1;
	}

	public static TimeService getTimeService(int speed,int duration){
		if (instance==null){
			instance=new TimeService(speed,duration);
		}
		return instance;
	}

	@Override
	protected void initialize() {
		TimeUnit unit=TimeUnit.MILLISECONDS;
	//	timer.scheduleAtFixedRate(timerTask,speedTime,durationTime);  //time clockOn
		while (currentTick<=durationTime){   //send the current tick to everyone that intrested in this broadcust
				sendBroadcast(new TickBroadcast(currentTick));
			try{	wait(unit.toMillis(speedTime));} catch (InterruptedException e){}
			currentTick++;
		}
		sendBroadcast(new TickFinalBroadcast(currentTick));  //send the final Tick
	}
}
