
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
    private static Vector<MicroService> microServices;
    //fields i added
    private static BookInventoryInfo[] initInventory;
    private static DeliveryVehicle[] initResources;
    //Schedule services
    private static Vector<Thread> threads;
    private static CountDownLatch latch;   //TODO - Schedule services static ???
    private static int countThreads;


    public static void main(String[] argss) throws IOException {

        String[] args = new String[5];
        args[0] = "src/main/java/sample.json";  //the location of the json file input

        readJsonAndLoad(args[0]);  //Load the program

        runServices(microServices); //run the program

        stopProgram(args);  //Stop the program
    }

    //------------Load-----------------------------//
    private static void readJsonAndLoad(String jsonFileName) {
        JsonParser parser = new JsonParser();
        JsonObject jObj = null;
        try {
            jObj = (JsonObject) parser.parse(new FileReader(jsonFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonObject rootObject = jObj.getAsJsonObject(); //the first input

        microServices = new Vector<>();
        inventory = Inventory.getInstance();
        resource = ResourcesHolder.getInstance();
        register = MoneyRegister.getInstance();
        reciepts = new LinkedList<>();
        threads = new Vector<>();
        countThreads = countServices(rootObject);
        latch = new CountDownLatch(countThreads);

        loadinventory(rootObject);
        loadResource(rootObject);

        loadServices(rootObject);
        loadCustomers(rootObject);
    }

    private static int countServices(JsonObject jobj) {
        JsonObject services = jobj.getAsJsonObject("services");
        int countSellings = services.get("selling").getAsInt();
        int countInventory = services.get("inventoryService").getAsInt();
        int countLogistics = services.get("logistics").getAsInt();
        int countResorces = services.get("resourcesService").getAsInt();
        JsonArray customerArray = services.getAsJsonArray("customers");
        int countAPI = customerArray.size();
        return countSellings + countInventory + countLogistics + countResorces + countAPI;
    }

    private static void loadResource(JsonObject jobj) {
        JsonArray resourceArray = jobj.getAsJsonArray("initialResources");
        JsonArray vehiclesArray = resourceArray.get(0).getAsJsonObject().getAsJsonArray("vehicles");

        initResources = new DeliveryVehicle[vehiclesArray.size()];

        /** take from json the vehicles and their fields
         */
        int license;
        int speed;
        for (int i = 0; i < vehiclesArray.size(); i++) {
            JsonObject text = vehiclesArray.get(i).getAsJsonObject();
            license = text.get("license").getAsJsonPrimitive().getAsInt();
            speed = text.get("speed").getAsJsonPrimitive().getAsInt();
            DeliveryVehicle newVehicle = new DeliveryVehicle(license, speed);
            initResources[i] = newVehicle;
        }
        resource.load(initResources);
    }

    private static void loadServices(JsonObject jobj) {
        JsonObject services = jobj.getAsJsonObject("services");
        /** we initial all Micro services here
         *
         */

        int speedTime;
        int duration;
        JsonObject time = services.getAsJsonObject("time");
        speedTime = time.get("speed").getAsJsonPrimitive().getAsInt();
        duration = time.get("duration").getAsJsonPrimitive().getAsInt();

        MicroService timeService = new TimeService(speedTime, duration);
        microServices.add(timeService);

        //microservice Selling Service and its initial
        int countSellings = services.get("selling").getAsInt();

        for (int i = 0; i < countSellings; i++) microServices.add(new SellingService(i + 1, latch));

        //microservice Inventory Service and its initial
        int countInventory = services.get("inventoryService").getAsInt();

        for (int i = 0; i < countInventory; i++) microServices.add(new InventoryService(i + 1, latch));

        //microservice Logistic Service and its initial
        int countLogistics = services.get("logistics").getAsInt();

        for (int i = 0; i < countLogistics; i++) microServices.add(new LogisticsService(i + 1, latch));

        //microservice resource Service and its initial
        int countResorces = services.get("resourcesService").getAsInt();

        for (int i = 0; i < countResorces; i++) microServices.add(new ResourceService(i + 1, latch));
    }

    private static void loadCustomers(JsonObject jobj) {
        JsonObject location = jobj.getAsJsonObject("services");
        JsonArray customerArray = location.getAsJsonArray("customers");
        JsonObject customer;
        JsonArray ordersArray;
        JsonObject order;

        customers = new HashMap<>();

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
            id = text.get("id").getAsJsonPrimitive().getAsInt();
            name = text.get("name").getAsJsonPrimitive().getAsString();
            address = text.get("address").getAsJsonPrimitive().getAsString();
            distance = text.get("distance").getAsJsonPrimitive().getAsInt();
            //object credit card
            numberCredit = text.get("creditCard").getAsJsonObject().getAsJsonPrimitive("number").getAsInt();
            amountCredit = text.get("creditCard").getAsJsonObject().getAsJsonPrimitive("amount").getAsInt();

            Customer newCustomer = new Customer(name, id, address, distance, numberCredit, amountCredit);
            customers.put(newCustomer.getId(), newCustomer);

            HashMap<Integer, Vector<String>> ordersCustomer = new HashMap<>(); // define the orders of the customers
            //array order schedule

            ordersArray = text.get("orderSchedule").getAsJsonArray();
            for (int j = 0; j < ordersArray.size(); j++) {
                order = ordersArray.get(j).getAsJsonObject();
                bookTitleOrder = order.get("bookTitle").getAsJsonPrimitive().getAsString();
                tick = order.get("tick").getAsJsonPrimitive().getAsInt();
                Integer tickKey = new Integer(tick);
                ordersCustomer.putIfAbsent(tickKey, new Vector<String>());
                ordersCustomer.get(tickKey).add(bookTitleOrder); //put new book in tickKey
            }
            microServices.add(new APIService(newCustomer, ordersCustomer, i + 1, latch));
        }
    }

    private static void loadinventory(JsonObject jobj) {
        JsonArray booksArray = jobj.getAsJsonArray("initialInventory");

        /** take from json the Books and their fields
         */
        initInventory = new BookInventoryInfo[booksArray.size()];
        String bookTitle;
        int amount;
        int price;
        //get the elemnts of the book from the books array
        for (int i = 0; i < booksArray.size(); i++) {
            JsonObject text = booksArray.get(i).getAsJsonObject();

            bookTitle = text.get("bookTitle").getAsJsonPrimitive().getAsString();
            amount = text.get("amount").getAsJsonPrimitive().getAsInt();
            price = text.get("price").getAsJsonPrimitive().getAsInt();

            BookInventoryInfo newInventoryInfo = new BookInventoryInfo(price, amount, bookTitle);
            initInventory[i] = newInventoryInfo;
        }
        inventory.load(initInventory);
    }

    //------------Run-----------------------------------//
    private static void runServices(Vector<MicroService> microServices) {
        MicroService timeSer = (MicroService) microServices.get(0);  //put the TimeService first on the threads vector.
        threads.add(new Thread(timeSer));  //now the tieservice is the first thread
        for (int i = 1; i < microServices.size(); i++) {
            MicroService m = (MicroService) microServices.get(i);
            threads.add(new Thread(m));
            threads.get(i).start(); //starts the threads
        }

        try {    //Schedule the threads
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threads.get(0).start();
    }


    //------------Stop-----------------------------//
    private static void stopProgram(String[] args) {

        args[1] = "Customers";
        args[2] = "Books";
        args[3] = "OrderReceipts";
        args[4] = "MoneyRegister";

        //kill All threads
            for (Thread thread:threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        System.out.println("All threads are dead");

        //Prints all Outputs
        printCustomersToFile(args[1]);
        inventory.printInventoryToFile(args[2]);
        register.printOrderReceipts(args[3]);
        printMoneyRegisterObject(args[4]);
    }

    //------------Prints To Files-----------------------------//

    private static void printCustomersToFile(String filename) {
        try {
            FileOutputStream outputF = new FileOutputStream(filename);
            ObjectOutputStream outputStream = new ObjectOutputStream(outputF);
            outputStream.writeObject(customers);
            outputStream.close();
            outputF.close();
        } catch (IOException I) {
            I.printStackTrace();
        }
    }

    private static void printMoneyRegisterObject(String filename) {
        try {
            FileOutputStream outputF = new FileOutputStream(filename);
            ObjectOutputStream outputStream = new ObjectOutputStream(outputF);
            outputStream.writeObject(register);
            outputStream.close();
            outputF.close();
        } catch (IOException I) {
            I.printStackTrace();
        }
    }
}










//        try {
//            endSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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