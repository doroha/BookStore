
package bgu.spl.mics.application;
        import bgu.spl.mics.MicroService;
        import bgu.spl.mics.application.passiveObjects.*;
        import bgu.spl.mics.application.services.*;
        import com.google.gson.*;
        import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;

        import java.io.*;
        import java.util.*;
        import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    @SuppressWarnings("unchecked")

    private static HashMap<Integer, Customer> customers;
    private static List<OrderReceipt> reciepts;
    private static MoneyRegister register;
    private static Inventory inventory;
//    private static HashMap<Integer,String> ordersCustomer;  //Customer's orders
    private static ResourcesHolder resource;
    private static Vector<MicroService>microServices;
    //fields i added
    private static BookInventoryInfo[] initInventory;
    private static DeliveryVehicle[] initResources;
    private static boolean stop=false;
    private static Vector<Thread> threads;
    private static int countThreads;
    private static CountDownLatch startSignal;   //TODO - Schedule services static ???
    private static  CountDownLatch endSignal;


    public static void main(String[] argss) throws IOException {

        String[]args=new String[5];
        args[0]= "src/main/java/sample.json";  //the location of the json file input

        startSignal=new CountDownLatch(1);
        endSignal = new CountDownLatch(countThreads);
        readJsonAndLoad(args[0]);
        runServices(microServices);
        stopProgram();
    }
//------------Load-----------------------------//
    private static void readJsonAndLoad(String jsonFileName) {
        JsonParser parser = new JsonParser();
        JsonObject jObj = null;
        try{
            jObj = (JsonObject) parser.parse(new FileReader(jsonFileName));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        JsonObject rootObject = jObj.getAsJsonObject(); //the first input

        inventory=Inventory.getInstance();
        resource=ResourcesHolder.getInstance();
        register=MoneyRegister.getInstance();
        reciepts=new LinkedList<>();
        threads=new Vector<>();

        loadinventory(rootObject);
        loadResource(rootObject);
        loadServices(rootObject);
        loadCustomers(rootObject);
    }

    private static void loadResource(JsonObject jobj){
        JsonArray resourceArray = jobj.getAsJsonArray("initialResources");
        JsonArray vehiclesArray = resourceArray.get(0).getAsJsonObject().getAsJsonArray("vehicles");

        initResources=new DeliveryVehicle[vehiclesArray.size()];

        /** take from json the vehicles and their fields
         */
        int license;
        int speed;
        for (int i=0;i<vehiclesArray.size();i++){
            JsonObject text = vehiclesArray.get(i).getAsJsonObject();
            license=text.get("license").getAsJsonPrimitive().getAsInt();
            speed=text.get("speed").getAsJsonPrimitive().getAsInt();
            DeliveryVehicle newVehicle= new DeliveryVehicle(license, speed);
            initResources[i]=newVehicle;
        }
        resource.load(initResources);
    }

    private static void loadServices(JsonObject jobj){
        JsonObject services = jobj.getAsJsonObject("services");
        /** we initial all Micro services here
         *
         */
        microServices = new Vector<>();
        int speedTime;
        int duration;
        JsonObject time = services.getAsJsonObject("time");
        speedTime=time.get("speed").getAsJsonPrimitive().getAsInt();
        duration=time.get("duration").getAsJsonPrimitive().getAsInt();

        TimeService timeService= new TimeService(speedTime,duration);
        microServices.add(timeService);

        //microservice Selling Service and its initial
        int countSellings = services.get("selling").getAsInt();

        for (int i = 0; i < countSellings; i++) microServices.add(new SellingService(i+1));

        //microservice Inventory Service and its initial
        int countInventory = services.get("inventoryService").getAsInt();

        for (int i = 0; i < countInventory; i++) microServices.add(new InventoryService(i+1));

        //microservice Logistic Service and its initial
        int countLogistics = services.get("logistics").getAsInt();

        for (int i = 0; i < countLogistics; i++) microServices.add(new LogisticsService(i+1));

        //microservice resource Service and its initial
        int countResorces = services.get("resourcesService").getAsInt();

        for (int i = 0; i < countResorces; i++) microServices.add(new ResourceService(i+1));
    }
    private static void loadCustomers(JsonObject jobj){
        JsonObject location = jobj.getAsJsonObject("services");
        JsonArray customerArray=location.getAsJsonArray("customers");
        JsonObject customer;
        JsonArray ordersArray;
        JsonObject order;

        customers=new HashMap<>();

        //getting the fields of the customer from the customer array
        int id;
        String name;
        String address;
        int distance;
        int numberCredit;
        int amountCredit;
        String bookTitleOrder;
        int tick;

        for (int i = 0; i < customerArray.size(); i++) {

            JsonObject text = customerArray.get(i).getAsJsonObject();
            id=text.get("id").getAsJsonPrimitive().getAsInt();
            name= text.get("name").getAsJsonPrimitive().getAsString();
            address=text.get("address").getAsJsonPrimitive().getAsString();
            distance=text.get("distance").getAsJsonPrimitive().getAsInt();
            //object credit card
            numberCredit=text.get("creditCard").getAsJsonObject().getAsJsonPrimitive("number").getAsInt();
            amountCredit=text.get("creditCard").getAsJsonObject().getAsJsonPrimitive("amount").getAsInt();

            Customer newCustomer= new Customer(name, id, address, distance,numberCredit,amountCredit);
            customers.put(newCustomer.getId(), newCustomer);

            HashMap<Integer,String> ordersCustomer=new HashMap<>(); // define the orders of the customers
            //array order schedule
            ordersArray= text.get("orderSchedule").getAsJsonArray();
            for(int j=0; j< ordersArray.size() ; j++){
                order = ordersArray.get(j).getAsJsonObject();
                bookTitleOrder= order.get("bookTitle").getAsJsonPrimitive().getAsString();
                tick= order.get("tick").getAsJsonPrimitive().getAsInt();
                ordersCustomer.put(new Integer(tick),bookTitleOrder);
            }
            microServices.add(new APIService(newCustomer,ordersCustomer,i+1));
        }
    }


    private static void loadinventory(JsonObject jobj){
        JsonArray booksArray = jobj.getAsJsonArray("initialInventory");

        /** take from json the Books and their fields
         */
        initInventory =new BookInventoryInfo[booksArray.size()];
        String bookTitle;
        int amount;
        int price;
        //get the elemnts of the book from the books array
        for (int i=0;i<booksArray.size();i++){
            JsonObject text = booksArray.get(i).getAsJsonObject();

            bookTitle=text.get("bookTitle").getAsJsonPrimitive().getAsString();
            amount =text.get("amount").getAsJsonPrimitive().getAsInt();
            price= text.get("price").getAsJsonPrimitive().getAsInt();

            BookInventoryInfo newInventoryInfo= new BookInventoryInfo(price, amount,bookTitle);
            initInventory[i]= newInventoryInfo;
        }
        inventory.load(initInventory);
    }

    //------------Run-----------------------------------//
    private static void runServices(Vector<MicroService>microServices){   //TODO - dtart
        MicroService timeSer= (MicroService)microServices.get(0);  //put the TimeService first on the threads vector.
        threads.add(new Thread(timeSer));  //now the tieservice is the first thread
        for(int i=1; i<microServices.size();i++){
            MicroService m= (MicroService)microServices.get(i);
            threads.add(new Thread(m));
            threads.get(i).start(); //starts the threads
        }
        threads.get(0).start();             //run the TimeService after all the other microServices

        countThreads=microServices.size(); //after start the services lets schedule them
        endSignal=new CountDownLatch(countThreads);

        startSignal.countDown(); // let all threads proceed
    }

    //------------Stop-----------------------------//
    private static void stopProgram() {
        try {
            endSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}














//    private static void function(String []paths, HashMap<Integer, Customer> customers) {
//        Customer p = new Customer("lynn" , 31909140, "Ben Zvi", 43, 12, 3);
////        String fileName = "data.bin";
//        Gson gson =new Gson();
//
//        try{
//            Writer write=new FileWriter(paths[0]);
//            gson.toJson(customers, write);
//            write.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//        Inventory inventory=Inventory.getInstance();
//        inventory.printInventoryToFile(paths[2]);
//        MoneyRegister register=MoneyRegister.getInstance();
//        register.printOrderReceipts(paths[4]);
//        try{
//            Writer write=new FileWriter(paths[3]);
//            gson.toJson(register, write);
//            write.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//        Customer cus= customers.get(234567891);
//
//        System.out.println(p.getName());
//        System.out.println(cus.getName());
//    }
// function(args, customers);

//        args[1]="customers.json";
//        args[2]="inventory.json";
//        args[3]="reciepts.json";
//        args[4]="register.json";