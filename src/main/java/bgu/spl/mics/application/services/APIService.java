package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.BookOrderEvent;
import bgu.spl.mics.application.Messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;
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
	private int countOrders;

	public APIService(Customer customer,HashMap orderSchedule) {
		super("APIService");
		this.customer=customer;
		this.orders=orderSchedule;
		this.countOrders=orderSchedule.size();
	}
	@Override
	protected void initialize() {

		if (orders.size()==0) terminate();

		Callback<TickBroadcast> sendBookOrderEvent= (TickBroadcast tick) -> {
			if (orders.containsKey(tick.getTick())) {
				countOrders--;
					Future<OrderReceipt> future = (Future<OrderReceipt>) sendEvent(new BookOrderEvent<OrderReceipt>(customer,orders.get(tick.getTick())));
					OrderReceipt receipt;
					if (future != null) {
							receipt = future.get();
							if (receipt != null) {
							System.out.println("The Order is done"); // for us to dubug later
						} else {
							System.out.println("The Order Fail");
						}
					}
					if (countOrders==0) terminate();
			}
			};
		subscribeBroadcast(TickBroadcast.class,sendBookOrderEvent);
	}
}










