package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.BookOrderEvent;
import bgu.spl.mics.application.Messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;
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
public class SellingService extends MicroService{

	private MoneyRegister moneyRegister;


	public SellingService() {
		super("SellingService");
		this.moneyRegister=MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {

		Callback<BookOrderEvent> bookSell= (BookOrderEvent b) -> {
			Future<BookInventoryInfo> available = (Future<BookInventoryInfo>) sendEvent(new CheckAvailabilityEvent<BookInventoryInfo>(b.getBook()));
			BookInventoryInfo book=available.get();
			if (book != null) {
				moneyRegister.chargeCreditCard(b.getCustomer(), book.getPrice());
				OrderReceipt result = new OrderReceipt(b.getCustomer().getId(), book.getBookTitle(), book.getPrice(), 100000, 10000, 10000);  /////TODO - values for the tick
				complete(b, result);
			}
			else complete(b,null);
		};
		subscribeEvent(BookOrderEvent.class,bookSell);

	}

}
