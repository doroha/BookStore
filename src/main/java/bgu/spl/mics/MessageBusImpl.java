package bgu.spl.mics;

import bgu.spl.mics.application.Messages.TickFinalBroadcast;

import java.util.*;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	static private MessageBusImpl instance;
	private ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> msgEvent_Hashmap; //event -> MicroServices
	private ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcast_Hashmap; //broadcast -> MicroServices
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceMsg_HashMap; //MicroService -> Message
	private ConcurrentHashMap<Event, Future> eventFutre_HashMap; //Event -> Future

	public static MessageBusImpl getInstance() {
		return SingletonHld.instanceNew;
	}

	private static class SingletonHld {
		private static MessageBusImpl instanceNew = new MessageBusImpl();
	}

	private MessageBusImpl() {
		msgEvent_Hashmap = new ConcurrentHashMap<>();
		broadcast_Hashmap = new ConcurrentHashMap<>();
		microServiceMsg_HashMap = new ConcurrentHashMap<>();
		eventFutre_HashMap = new ConcurrentHashMap<>();
	}

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		msgEvent_Hashmap.putIfAbsent(type, new LinkedBlockingQueue<>());
		try {
			msgEvent_Hashmap.get(type).put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcast_Hashmap.putIfAbsent(type, new LinkedBlockingQueue<>());
		try {
			broadcast_Hashmap.get(type).put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public <T> void complete(Event<T> e, T result) {
		eventFutre_HashMap.get(e).resolve(result);
	}

	public void sendBroadcast(Broadcast b) {
		broadcast_Hashmap.putIfAbsent(b.getClass(), new LinkedBlockingQueue<>());
		if (broadcast_Hashmap.get(b.getClass()).isEmpty()) return;
		if (b.getClass().getClass().equals(TickFinalBroadcast.class)){
			//TODO- termination of the services one by one
		}
		for (MicroService m : broadcast_Hashmap.get(b.getClass())) {
			try {
				microServiceMsg_HashMap.get(m).put(b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {  //Round Robin
		msgEvent_Hashmap.putIfAbsent(e.getClass(), new LinkedBlockingQueue<>());
		if (msgEvent_Hashmap.get(e.getClass()).isEmpty()) return null;
		MicroService m = msgEvent_Hashmap.get(e.getClass()).poll();
		Future<T> f = new Future<>();
		eventFutre_HashMap.putIfAbsent(e, f);
		try {
			microServiceMsg_HashMap.get(m).put(e);
			msgEvent_Hashmap.get(e.getClass()).put(m);
		} catch (InterruptedException e1) {
		}
		return f;
	}

	@Override
	public void register(MicroService m) {   // synchronized ???
		microServiceMsg_HashMap.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {

		if (!microServiceMsg_HashMap.get(m).isEmpty()) {
			for (Message message : microServiceMsg_HashMap.get(m)) {
				eventFutre_HashMap.get(message).resolve(null);
				eventFutre_HashMap.remove(message);
			}
		}
			microServiceMsg_HashMap.remove(m);
			removeMicroEvent(m);
			removeMicroBroad(m);
		}

	public void removeMicroEvent(MicroService microService) {
		for (Class<? extends Event> event : msgEvent_Hashmap.keySet()) {
			for (MicroService m:msgEvent_Hashmap.get(event)){
				if (m.equals(microService)){
					msgEvent_Hashmap.get(event).remove(m);
					break;
				}
			}
		}
	}

	public void removeMicroBroad(MicroService microService) {
		for (Class<? extends Broadcast> broad : broadcast_Hashmap.keySet()) {
			for (MicroService m : broadcast_Hashmap.get(broad)) {
				if (m.equals(microService)) {
					broadcast_Hashmap.get(broad).remove(m);
					break;
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {  //Synchronized ???
		if (microServiceMsg_HashMap.get(m)==null) throw new IllegalStateException();
		return microServiceMsg_HashMap.get(m).take();
	}
}
