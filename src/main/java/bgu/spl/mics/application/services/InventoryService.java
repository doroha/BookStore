package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.BookOrderEvent;
import bgu.spl.mics.application.Messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.Messages.TakeEvent;
import bgu.spl.mics.application.Messages.TickFinalBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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
	private CountDownLatch latch;

	public InventoryService(int number,CountDownLatch lat) {
		super("InventoryService" + number);
		this.inventory=Inventory.getInstance();
		this.latch=lat;
	}

	@Override
	protected void initialize() {
		System.out.println(getName()+ " Hello Book Store");
		subscribeEvent(CheckAvailabilityEvent.class,(CheckAvailabilityEvent c) -> {  //check if the book is avialeble in the inventory and the get the price of this book
				System.out.println(getName() + " Check if there is book in inventory and reture his price");
				Integer price=new Integer(inventory.checkAvailabiltyAndGetPrice(c.getBookTitle()));
				complete(c, price);
		});

		subscribeEvent(TakeEvent.class,(TakeEvent take)->{   //take the book from the inventory if we can do so
			System.out.println(getName() + " Tried to take the book");
			OrderResult result=inventory.take(take.getBookTitle());
			complete(take,result);
		});

		subscribeBroadcast(TickFinalBroadcast.class,(TickFinalBroadcast tick)->{
			terminate();
		});
		latch.countDown();
	}
}
