package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
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

	public SellingService(int number) {
		super("SellingService" + number);
		this.moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println(getName()+ " Hello Book Store");
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent b) -> { //tries to performe the order

					Future<Integer> available = (Future<Integer>) sendEvent(new CheckAvailabilityEvent<Integer>(b.getBookTitle()));  //check if the book in Inventory
					if (available != null) {
						Integer price = available.get();
						if (price.intValue() == -1) {if (!b.getCustomer().possibleCharge(price.intValue()))
							complete(b, null);
						} else
							synchronized (b.getCustomer()) {   //lock the customer that no other customer else will charge
								if (b.getCustomer().possibleCharge(price.intValue())) {
									Future<OrderResult> orderResult = (Future<OrderResult>) sendEvent(new TakeEvent<OrderResult>(b.getBookTitle()));
									if (orderResult != null) {
										if (orderResult.get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
											if (b.getCustomer().possibleCharge(price.intValue())) { //check if mo one else charge meenwile this customer
												moneyRegister.chargeCreditCard(b.getCustomer(), price.intValue());
												OrderReceipt receipt = new OrderReceipt(getName(),b.getCustomer().getId(), b.getBookTitle(), price.intValue(),  b.getOrderTick(), b.getOrderTick(),  b.getOrderTick());
												moneyRegister.file(receipt);
												complete(b,receipt);
											} else {
												complete(b, null);  // it is not possible to charge the customer
											}
										} else {
											complete(b, null); // the order is NOT_IN_STOCK
										}
									}
								} else { // it is not possible to charge the customer
									complete(b, null);
								}
							}
					}
				});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick) ->{
				terminate();
		});
	}
}

