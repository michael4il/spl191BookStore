package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import bgu.spl.mics.application.passiveObjects.*;



import bgu.spl.mics.application.passiveObjects.Customer;


import javax.annotation.Resources;
import java.io.*;
import java.util.*;


import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
@SuppressWarnings("Duplicates")
public class BookStoreRunner {

    public static void main(String[] args) throws IOException {
        HashMap<Integer, Customer> customerHashMap = new HashMap<>();
        JsonObject jsoninput;
        JsonParser parser = new JsonParser();
        try (FileReader fileReader = new FileReader(args[0])) {
            jsoninput = parser.parse(fileReader).getAsJsonObject();
        } catch (IOException e) {
            throw e;
        }

        //*******************************************************INVENTORY*****************************************************
        JsonArray JInv = jsoninput.getAsJsonArray("initialInventory");
        BookInventoryInfo[] books = new BookInventoryInfo[JInv.size()];
        for (int i = 0; i < books.length; i++) {
            JsonObject book = JInv.get(i).getAsJsonObject();
            books[i] = new BookInventoryInfo(book.get("bookTitle").getAsString(), book.get("amount").getAsInt(), book.get("price").getAsInt());
        }
        Inventory.getInstance().load(books);

        //*********************************************************VEHICLES****************************************************
        JsonArray Jveihcles = jsoninput.getAsJsonArray("initialResources").get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
        DeliveryVehicle[] veichles = new DeliveryVehicle[Jveihcles.size()];
        for (int i = 0; i < veichles.length; i++) {
            JsonObject car = Jveihcles.get(i).getAsJsonObject();
            veichles[i] = new DeliveryVehicle(car.get("license").getAsInt(), car.get("speed").getAsInt());
        }
        ResourcesHolder.getInstance().load(veichles);

        //----------------------------------------------------------SERVICES--------------------------------------------------
        Vector<Thread> threadVector = new Vector<>();
        JsonObject Services = jsoninput.get("services").getAsJsonObject();
        int countOfThreads = Services.get("customers").getAsJsonArray().size()
                + Services.get("logistics").getAsInt()
                + Services.get("selling").getAsInt()
                + Services.get("resourcesService").getAsInt()
                + Services.get("inventoryService").getAsInt();
        CountDownLatch countDownLatch = new CountDownLatch(countOfThreads);


        //**********************************************************CUSTOMERS*****************************************************

        JsonArray Customers = Services.get("customers").getAsJsonArray();
        for (int i = 0; i < Customers.size(); i++) {
            List<Pair<String, Integer>> orderlist = new LinkedList<>();
            JsonArray Jorders = Customers.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray();
            for (int k = 0; k < Jorders.size(); k++) {
                JsonObject Jpair = Jorders.get(k).getAsJsonObject();
                Pair<String, Integer> pair = new Pair<>(Jpair.get("bookTitle").getAsString(), Jpair.get("tick").getAsInt());
                ((LinkedList<Pair<String, Integer>>) orderlist).push(pair);
            }

            JsonObject JC = Customers.get(i).getAsJsonObject();//Json Customer
            Customer Customer = new Customer(JC.get("id").getAsInt(), JC.get("name").getAsString(), JC.get("address").getAsString(), JC.get("distance").getAsInt(),
                    JC.get("creditCard").getAsJsonObject().get("number").getAsInt(),
                    JC.get("creditCard").getAsJsonObject().get("amount").getAsInt(), orderlist);

            APIService ApiService = new APIService(Customer, countDownLatch);
            customerHashMap.put(Customer.getId(), Customer);
            Thread ApiThread = new Thread(ApiService);
            threadVector.add(ApiThread);

            ApiThread.start();


        }
        int sellingCount = Services.get("selling").getAsInt();
        for (int i = 0; i < sellingCount; i++) {
            SellingService sellingService = new SellingService(i, countDownLatch);
            Thread sellingThread = new Thread(sellingService);
            threadVector.add(sellingThread);
            sellingThread.start();

        }
        //*************************************************************LOGISTICS****************************************************************
        int LogisticCount = Services.get("logistics").getAsInt();
        for (int i = 0; i < LogisticCount; i++) {
            LogisticsService logisticsService = new LogisticsService(i, countDownLatch);
            Thread logisticThread = new Thread(logisticsService);
            threadVector.add(logisticThread);
            logisticThread.start();
        }

        int resourcesCount = Services.get("resourcesService").getAsInt();
        for (int i = 0; i < resourcesCount; i++) {
            ResourceService resourceService = new ResourceService(i, countDownLatch);
            Thread resourceThread = new Thread(resourceService);
            threadVector.add(resourceThread);
            resourceThread.start();
        }


        int inventoryCount = Services.get("inventoryService").getAsInt();
        for (int i = 0; i < inventoryCount; i++) {
            InventoryService inventoryService = new InventoryService(i, countDownLatch);
            Thread inventoryThread = new Thread(inventoryService);
            threadVector.add(inventoryThread);
            inventoryThread.start();
        }

        //******************************************************************TIME******************************************************************


        Services.get("time").getAsJsonObject().get("speed").getAsInt();
        Services.get("time").getAsJsonObject().get("duration").getAsInt();
        TimeService timeService = new TimeService(Services.get("time").getAsJsonObject().get("speed").getAsInt(), Services.get("time").getAsJsonObject().get("duration").getAsInt(), countDownLatch);
        Thread timeThread = new Thread(timeService);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
        }
        timeThread.start();


        //*************************************************************END SERVICES******************************************************************
        try {
            for (Thread thread : threadVector)
                thread.join();
        } catch (InterruptedException e) {
        }
        System.out.println();

//----------------------------------------------------------------------------PARSING------------------------------------------------------------------

        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);


        try {
            FileOutputStream fileStream = new FileOutputStream(args[1]);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(customerHashMap);
            objectStream.close();
            fileStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            FileOutputStream fileStream = new FileOutputStream(args[4]);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(MoneyRegister.getInstance());
            objectStream.close();
            fileStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}





