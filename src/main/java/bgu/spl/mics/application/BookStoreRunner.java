
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
    private static HashMap<String, Integer> booksHashmap;
    private static MoneyRegister register;
    private static Inventory inventory;
    private static HashMap<Integer,String> ordersCustomer;  //Customer's orders
    private static ResourcesHolder resource;
    private static Vector<MicroService>microServices;

    public static void main(String[] args) {

        microServices = new Vector<>();
        customers =new HashMap<>();
        reciepts=new Vector<>();
        booksHashmap=new HashMap<>();
        register=MoneyRegister.getInstance();
        inventory=Inventory.getInstance();
        ordersCustomer=new HashMap<>();
        resource=ResourcesHolder.getInstance();
        microServices=new Vector<>();

        JsonParser parser = new JsonParser();
        InputStream inputStream = BookStoreRunner.class.getClassLoader().getResourceAsStream("sample.json");
        Reader reader = new InputStreamReader(inputStream);
        parser.parse(reader);
        JsonElement rootElement = parser.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();
//        JsonObject books=rootObject.getAsJsonObject("initialInventory");
        JsonArray booksArray = null;
        for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            booksArray = entryObject.getAsJsonArray("initialInventory");
        }

        JsonArray resourceArray = null;
        for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            resourceArray = entryObject.getAsJsonArray("initialResources");
        }

        JsonObject services = rootObject.getAsJsonObject("services");

        JsonObject time = services.getAsJsonObject("time");
      //  TimeService timeService = new TimeService(time.get("speed").getAsInt(), time.get("duration").getAsInt());

        int countSellings = services.get("selling").getAsInt();   //TODO - how to get the int of the element

        for (int i = 0; i < countSellings; i++) microServices.addElement(new SellingService());


        int countInventory = services.get("inventoryService").getAsInt();

        for (int i = 0; i < countInventory; i++) microServices.addElement(new InventoryService());


        int countLogistics = services.get("logistics").getAsInt();

        for (int i = 0; i < countLogistics; i++) microServices.addElement(new LogisticsService());

        int countResorces = services.get("resourcesService").getAsInt();

        for (int i = 0; i < countResorces; i++) microServices.addElement(new ResourceService());

        JsonArray customerArray = null;
        for (Map.Entry<String, JsonElement> entry : rootObject.getAsJsonObject("services").entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            customerArray = entryObject.getAsJsonObject("services").getAsJsonArray("customers");
        }

        for (int i = 0; i < customerArray.size(); i++) {
            //
        }

        //hashmap -> id  - customer
        HashMap<Integer, Customer> customers = new HashMap<>();

        // List ->  recipts
        List<OrderReceipt> reciepts = new LinkedList<>();

        // HashMap -> namebook - how many books in the inventory
        HashMap<String, Integer> booksHashmap = new HashMap<>();

        //one instnce of the moneyRegister
        MoneyRegister register = MoneyRegister.getInstance();

        Inventory inventory = Inventory.getInstance();

        ResourcesHolder resource = ResourcesHolder.getInstance();

        int countApi = customerArray.size();
    //    for (int i = 0; i < countApi; i++) microServices.addElement(new APIService());

        try {
            Object obj = parser.parse(new FileReader(("/Users/Lynn N/Desktop/BookStore-master/BookStore-master/src/main/java/bgu/spl/mics/application/Config.txt")));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
