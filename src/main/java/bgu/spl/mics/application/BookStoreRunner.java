
package bgu.spl.mics.application;
        import bgu.spl.mics.MicroService;
        import bgu.spl.mics.application.passiveObjects.*;
        import bgu.spl.mics.application.services.*;
        import jdk.nashorn.internal.parser.JSONParser;
        import com.google.gson.*;
        import java.io.*;
        import java.util.*;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    @SuppressWarnings("unchecked")

    private static HashMap<Integer, Customer> customers;
    private static Vector<OrderReceipt> reciepts;
    private static MoneyRegister register;
    private static Inventory inventory;
    private static HashMap<Integer,String> ordersCustomer;  //Customer's orders
    private static ResourcesHolder resource;
    private static Vector<MicroService>microServices;
    //fields i added
    private static BookInventoryInfo[] initInventory;
    private static DeliveryVehicle[] initResources;


    public static void main(String[] args) {

        String input = "src\\main\\java\\bgu\\spl\\mics\\application\\sample.json t1 t2 t3";
        String[] veriable = input.split("\\s+");

            String jsonFile = veriable[0];
//            String customerMap = veriable[1];
//            String bookMap = veriable[2];
//            String orderReceipts = veriable[3];
            readJsonAndLoad(jsonFile);

    }

    private static void readJsonAndLoad(String jsonFile) {
        JsonParser parser = new JsonParser();
        JsonObject jObj = null;
        try{
            jObj = (JsonObject) parser.parse(new FileReader(jsonFile));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        JsonObject rootObject = jObj.getAsJsonObject();

        customers =new HashMap<>();
        reciepts=new Vector<>();
        register=MoneyRegister.getInstance();
        inventory=Inventory.getInstance();
        ordersCustomer=new HashMap<>();
        resource=ResourcesHolder.getInstance();
        microServices = new Vector<>();
        inventory.load(initInventory);
        resource.load(initResources);

        JsonArray booksArray = rootObject.getAsJsonArray("initialInventory");

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

        JsonArray resourceArray = rootObject.getAsJsonArray("initialResources");
        JsonArray vehiclesArray = resourceArray.get(0).getAsJsonObject().getAsJsonArray("vehicles");

        initResources=new DeliveryVehicle[vehiclesArray.size()];

        //get the elements of the resource from the resource array
        int license;
        int speed;
        for (int i=0;i<vehiclesArray.size();i++){
            JsonObject text = vehiclesArray.get(i).getAsJsonObject();

            license=text.get("license").getAsJsonPrimitive().getAsInt();
            speed=text.get("speed").getAsJsonPrimitive().getAsInt();

            DeliveryVehicle newVehicle= new DeliveryVehicle(license, speed);
            initResources[i]=newVehicle;
        }
        JsonObject services = rootObject.getAsJsonObject("services");

        //microservice time and its fields
        int speedTime;
        int duration;
        JsonObject time = services.getAsJsonObject("time");
        speedTime=time.get("speed").getAsJsonPrimitive().getAsInt();
        duration=time.get("duration").getAsJsonPrimitive().getAsInt();

        TimeService newTimeService= TimeService.getTimeService(speedTime,duration);

        //microservice Selling Service and its initial
        int countSellings = services.get("selling").getAsInt();

        for (int i = 0; i < countSellings; i++) microServices.addElement(new SellingService(i));

        //microservice Inventory Service and its initial
        int countInventory = services.get("inventoryService").getAsInt();

        for (int i = 0; i < countInventory; i++) microServices.addElement(new InventoryService(i));

        //microservice Logistic Service and its initial
        int countLogistics = services.get("logistics").getAsInt();

        for (int i = 0; i < countLogistics; i++) microServices.addElement(new LogisticsService(i));

        //microservice resource Service and its initial
        int countResorces = services.get("resourcesService").getAsInt();

        for (int i = 0; i < countResorces; i++) microServices.addElement(new ResourceService(i));



        JsonArray customerArray = services.getAsJsonObject().getAsJsonArray("customers");
        JsonObject customer;
        JsonArray ordersArray;
        JsonObject orde;

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

            //array order schedule
            ordersArray= text.getAsJsonArray("orderSchedule");
            for(int j=0; j< ordersArray.size() ; j++){
                orde = ordersArray.get(j).getAsJsonObject();
                bookTitleOrder= orde.get("bookTitle").getAsJsonPrimitive().getAsString();
                tick= orde.get("tick").getAsJsonPrimitive().getAsInt();
                ordersCustomer.put(tick,bookTitleOrder);
            }
        }
    }
}
