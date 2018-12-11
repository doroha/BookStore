package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	static private MessageBusImpl instance;
	private ConcurrentHashMap<Class<?>,BlockingQueue<MicroService>> msgEvent_Hashmap; //event -> MicroServices
	private ConcurrentHashMap<Class<?>,BlockingQueue<MicroService>> broadcast_Hashmap; //broadcast -> MicroServices
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> microServiceMsg_HashMap; //MicroService -> Message
	private ConcurrentHashMap<Class<? extends Event>, Future> eventFutre_HashMap; //Event -> Future
	private Object lock1=new Object();
	private Object lock2=new Object();

	public static MessageBusImpl getInstance(){
			return SingletonHld.instanceNew;
	}
	private static class SingletonHld{
		private static MessageBusImpl instanceNew= new MessageBusImpl();
	}

	private MessageBusImpl(){
		msgEvent_Hashmap=new ConcurrentHashMap<>();
		broadcast_Hashmap=new ConcurrentHashMap<>();
		microServiceMsg_HashMap=new ConcurrentHashMap<>();
		eventFutre_HashMap=new ConcurrentHashMap<>();
	}

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (microServiceMsg_HashMap.contains(m)) {
			if (msgEvent_Hashmap.contains(type.getClass()) && !msgEvent_Hashmap.get(type).contains(m)) {
				try {
					msgEvent_Hashmap.get(type).put(m);
				} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				synchronized (lock1) {
					BlockingQueue<MicroService> new_BlockingQueue = new LinkedBlockingQueue<>();
					try {
						new_BlockingQueue.put(m);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					msgEvent_Hashmap.putIfAbsent(type, new_BlockingQueue);
				}
			}
		}
	}

	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (microServiceMsg_HashMap.contains(m) && !broadcast_Hashmap.get(type).contains(m)){
			try {
				broadcast_Hashmap.get(type).put(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			synchronized (lock2) {
				BlockingQueue<MicroService> new_BlockingQueue = new LinkedBlockingQueue<>();
				try {
					new_BlockingQueue.put(m);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				broadcast_Hashmap.putIfAbsent(type, new_BlockingQueue);
			}
		}
	}

	public <T> void complete(Event<T> e, T result) {
		if (eventFutre_HashMap.contains(e)) eventFutre_HashMap.get(e).resolve(result);
	}

	public void sendBroadcast(Broadcast b) {
            broadcast_Hashmap.putIfAbsent(b.getClass(),new LinkedBlockingQueue<>());
            if (broadcast_Hashmap.get(b).isEmpty()) return;
			for (MicroService m : broadcast_Hashmap.get(b))
				try {
					microServiceMsg_HashMap.get(m).put(b);
					notifyAll();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		msgEvent_Hashmap.putIfAbsent(e.getClass(),new LinkedBlockingQueue<>());
		MicroService m= msgEvent_Hashmap.get(e.getClass()).peek();
		Future<T> f=new Future<>();
		eventFutre_HashMap.putIfAbsent(e.getClass(),f);
		try {
			microServiceMsg_HashMap.get(m).put(e);
			notifyAll();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		msgEvent_Hashmap.get(e).poll();
		try {
			msgEvent_Hashmap.get(e).put(m);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return eventFutre_HashMap.get(e);
	}

	@Override
	public void register(MicroService m) {   // synchronized ???
			if (!microServiceMsg_HashMap.contains(m)) {
				BlockingQueue<Message> newBq = new LinkedBlockingQueue<>();
				microServiceMsg_HashMap.putIfAbsent(m, newBq);
			}
			}

	@Override
	public void unregister(MicroService m) {
		if (microServiceMsg_HashMap.contains(m)) {
			microServiceMsg_HashMap.remove(m);
			for (Map.Entry<Class<?>, BlockingQueue<MicroService>> microEvent : msgEvent_Hashmap.entrySet()) {
				if (microEvent.getValue().contains(m)) microEvent.getValue().remove(m);
				if (microEvent.getValue().size() == 0) {
					deleteEvent(microEvent.getKey(),m);
				}
			}
			}
		for (Map.Entry<Class<?>, BlockingQueue<MicroService>> broad : broadcast_Hashmap.entrySet()) {
			if (broad.getValue().contains(m)) broad.getValue().remove(m);
			if (broad.getValue().size() == 0){
				deletebroadcast(broad.getKey(),m);
			}
		}
	}

	public void deleteEvent(Class<?> event,MicroService m){
		msgEvent_Hashmap.remove(event);
		microServiceMsg_HashMap.get(m).remove(event);
		eventFutre_HashMap.remove(event);
	}

	public void deletebroadcast(Class<?> broad,MicroService m){
		broadcast_Hashmap.remove(broad);
		microServiceMsg_HashMap.get(m).remove(broad);
		eventFutre_HashMap.remove(broad);
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {  //Synchronized ???
		Message message;
		if (microServiceMsg_HashMap.get(m)==null) throw new IllegalStateException();
			while (microServiceMsg_HashMap.get(m).size() == 0) wait();
			message = microServiceMsg_HashMap.get(m).poll();
		return message;
	}
}
