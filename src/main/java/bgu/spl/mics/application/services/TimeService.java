package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.TickBroadcast;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


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
public class TimeService extends MicroService {


	private AtomicInteger currentTick;
	private int speedTime;
	private int duration;
	private Timer timer;
	private TimerTask timerTask;

	public TimeService(int speed, int duration) {
		super("Time Service");
		this.speedTime = speed;
		this.duration = duration;
		this.timer = new Timer();
		this.currentTick =new AtomicInteger(1);
	}

	@Override
	protected void initialize() {
		System.out.println(getName()+ " Hello Book Store");

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (currentTick.get() < duration) {
					System.out.println("Send Tick BroadCast: " + currentTick.get());
					sendBroadcast(new TickBroadcast(currentTick.getAndIncrement()));
				} else { //this the termination tick
					System.out.println("Termination tick: " + currentTick.get());
					sendBroadcast(new TickFinalBroadcast(currentTick.get()));
					timer.cancel();
				}
			}
		}, 0, speedTime);  //time clockOn TODO - how much delay we start the timer.
		subscribeBroadcast(TickFinalBroadcast.class, (TickFinalBroadcast tick) -> {this.terminate();});
	}
}









//
//	public static void main (String [] args){
//		TimeService timeService=new TimeService(1000,25);
//		timeService.initialize();
//	}
//}
//	TimeUnit unit=TimeUnit.MILLISECONDS;
//		while (currentTick<=duration){   //send the current tick to everyone that intrested in this broadcust
//			System.out.println(currentTick);
//			sendBroadcast(new TickBroadcast(currentTick));
//			try{	wait(unit.toMillis(speedTime));} catch (InterruptedException e){}
//			currentTick++;
//		}
//		sendBroadcast(new TickFinalBroadcast(currentTick));  //send the final Tick