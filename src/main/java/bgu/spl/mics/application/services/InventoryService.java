package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.BookOrderEvent;
import bgu.spl.mics.application.Messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private OrderResult positive;

	public InventoryService() {
		super("InventoryService");
		this.inventory=Inventory.getInstance();
		this.positive=OrderResult.valueOf("SUCCESSFULLY_TAKEN");
	}

	@Override
	protected void initialize() {

		Callback<CheckAvailabilityEvent> check= (CheckAvailabilityEvent c) -> {
			if (inventory==null){ System.out.print("Terminate"); terminate(); }

			if (inventory.isAvailable(c.getBook())){
				if (inventory.take(c.getBook()).equals(positive)){
					complete(c,inventory.getBook(c.getBook()));
				} else complete(c,null);   //the result is negativ
			}
		};
		subscribeEvent(CheckAvailabilityEvent.class,check);
	}
}
