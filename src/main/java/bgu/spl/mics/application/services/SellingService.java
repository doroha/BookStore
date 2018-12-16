package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

	private MoneyRegister moneyRegister;
	private CountDownLatch latch;

	public SellingService(int number,CountDownLatch lat) {
		super("SellingService" + number);
		this.moneyRegister = MoneyRegister.getInstance();
		this.latch=lat;
	}

	@Override
	protected void initialize() {
		System.out.println(getName()+ " Hello Book Store");
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent b) -> { //tries to performe the order

			System.out.println(getName() + "get BookorderEvent and send CheckAvailabilityEvent event");
			Future<Integer> available = (Future<Integer>) sendEvent(new CheckAvailabilityEvent<Integer>(b.getBookTitle()));  //check if the book in Inventory
			System.out.println(getName() + " Future available or null is comming");
			Integer price = null;
			if (available != null) {
				price = available.get();
				System.out.println(getName() + " there is result for the book");
				if (price.intValue() == -1 || !b.getCustomer().possibleCharge(price.intValue())) { //cant buy the book
					System.out.println(getName() + ": " + b.getCustomer().getName() + " can't buy the book - the book is not available or the customer's money is not enough");
					complete(b, null);
				} else {   // can buy the book
					synchronized (b.getCustomer()) {   //lock the customer that no other customer else will charge
						if (b.getCustomer().possibleCharge(price.intValue())) {
							Future<OrderResult> orderResult = (Future<OrderResult>) sendEvent(new TakeEvent<OrderResult>(b.getBookTitle()));
							System.out.println(getName() + " Future OrderResult or null is comming");
							if (orderResult != null) {
								if (orderResult.get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
									if (b.getCustomer().possibleCharge(price.intValue())) { //check if mo one else charge meenwile this customer
										System.out.println(getName() + " Now finally we can charge the customer: " + b.getCustomer().getName());
										moneyRegister.chargeCreditCard(b.getCustomer(), price.intValue());
										OrderReceipt receipt = new OrderReceipt(getName(), b.getCustomer().getId(), b.getBookTitle(), price.intValue(), b.getOrderTick(), b.getOrderTick(), b.getOrderTick());
										moneyRegister.file(receipt);
										b.getCustomer().file(receipt);
										System.out.println(getName() + " The charge is done and there is recipt for pruches");
										complete(b, receipt);
										//Send Delivery
										System.out.println(getName() + " send DeliveryEvent");
										sendEvent(new DeliveryEvent<DeliveryVehicle>(b.getCustomer().getDistance(), b.getCustomer().getAddress()));
									} else {
										System.out.println(getName() + " it is not possible to charge the customer");
										complete(b, null);  // it is not possible to charge the customer
									}
								} else {
									System.out.println(getName() + " the order is NOT_IN_STOCK");
									complete(b, null); // the order is NOT_IN_STOCK
								}
							}
						} else { // it is not possible to charge the customer
							System.out.println(getName() + " it is not possible to charge the customer");
							complete(b, null);
						}
					}
				}
			} else {
				System.out.println("No Micro-Service has registered to handle CheckAvailabilityEvent! The event cannot be processed");
				complete(b,null);
			}
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick) ->{
				terminate();
		});

		latch.countDown();
	}
}

