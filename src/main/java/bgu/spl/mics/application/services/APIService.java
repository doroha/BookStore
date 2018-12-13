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
import java.util.concurrent.ConcurrentHashMap;


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
	private HashMap<Integer,String> orders;

	public APIService(Customer customer,HashMap<Integer,String>orderSchedule) {
		super("APIService");
		this.customer=customer;
		this.orders=orderSchedule;
	}
	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick) -> {
			if (orders.containsKey(tick.getTick())) {
					BookOrderEvent<OrderReceipt> bookOrderEvent = new BookOrderEvent<OrderReceipt>(customer, orders.get(tick.getTick()),tick.getTick().intValue());
					Future<OrderReceipt> future = (Future<OrderReceipt>) sendEvent(bookOrderEvent);
					OrderReceipt receipt;
					if (future != null) {
						receipt = future.get();
						customer.file(receipt);
						sendEvent(new DeliveryEvent<DeliveryVehicle>(customer.getDistance(),customer.getAddress()));
						System.out.println("The Order is done"); // for us to dubug later
					}  else  System.out.println("The Order Fail");
			}
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick) ->{
			terminate();
		});
	}
}










