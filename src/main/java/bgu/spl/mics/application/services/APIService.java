package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.BookOrderEvent;
import bgu.spl.mics.application.Messages.DeliveryEvent;
import bgu.spl.mics.application.Messages.TickBroadcast;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private HashMap<Integer,Vector<String>> orders;
	private CountDownLatch latch;


	public APIService(Customer customer,HashMap<Integer,Vector<String>>orderSchedule,int number,CountDownLatch lat ) { // TODO - Latch
		super("APIService: "+ number);
		this.customer=customer;
		this.orders=orderSchedule;
		this.latch=lat;
	}
	@Override
	protected void initialize() {

		System.out.println(getName()+ " Hello Book Store");
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast t) -> {
			Integer tick=t.getTick();
			if (orders.containsKey(tick)){ //if i have an orders about this tick
					for (String bookTitle:orders.get(tick)) { //for all the orders that need proccess in the same tick
						BookOrderEvent<OrderReceipt> bookOrderEvent = new BookOrderEvent<OrderReceipt>(customer, bookTitle, tick.intValue());
						System.out.println(getName() + " send BookOrderEvent event");
						Future<OrderReceipt> future = (Future<OrderReceipt>) sendEvent(bookOrderEvent);
						OrderReceipt receipt;
						System.out.println(getName() + " recipt or null is coming");
						if (future != null) {
							receipt = future.get();
							customer.file(receipt);
							System.out.println("The Order is done and there is recipt"); // for us to dubug later
							System.out.println(getName() + " send DeliveryEvent ");
							sendEvent(new DeliveryEvent<DeliveryVehicle>(customer.getDistance(), customer.getAddress()));
						} else {
							System.out.println("No Micro-Service has registered to handle BookOrderEvent! The event cannot be processed");
						}
					}
			}
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick) ->{
			System.out.println(getName()+ " Terminated");
			terminate();
		});
		latch.countDown();
	}
}










