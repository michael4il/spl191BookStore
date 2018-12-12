package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.APIService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.*;
import javafx.util.Pair;
import bgu.spl.mics.application.passiveObjects.Customer;


import java.util.*;


import java.io.FileReader;
import java.io.IOException;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {//testing
    public static void main(String[] args) throws IOException{
        JsonObject jsoninput ;
        JsonParser parser = new JsonParser();
        try (FileReader fileReader = new FileReader(args[0]))
        { jsoninput=parser.parse(fileReader).getAsJsonObject();}
        catch (IOException e ) {throw e;}
        //**********************************************************INVENTORY**********************************************
        JsonArray JInv = jsoninput.getAsJsonArray("initialInventory");
        BookInventoryInfo[] books = new BookInventoryInfo[JInv.size()];
        for(int i=0;i<books.length;i++)
        {
            JsonObject book=JInv.get(i).getAsJsonObject();
            books[i]=new BookInventoryInfo(book.get("bookTitle").getAsString(),book.get("amount").getAsInt(),book.get("price").getAsInt());
        }
        Inventory.getInstance().load(books);

        //*********************************************************VEHICLES***********************************************
        JsonArray Jveihcles = jsoninput.getAsJsonArray("initialResources").get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
        DeliveryVehicle[] veichles = new DeliveryVehicle[Jveihcles.size()];
        for(int i=0;i<veichles.length;i++)
        {
            JsonObject car= Jveihcles.get(i).getAsJsonObject();
            veichles[i]=new DeliveryVehicle(car.get("license").getAsInt(),car.get("speed").getAsInt());
        }
        ResourcesHolder.getInstance().load(veichles);
        //********************************************************SERVICES********************************************

        //**********************************************CUSTOMERS*****************************************************
        JsonObject Services=jsoninput.get("services").getAsJsonObject();
        JsonArray Customers=Services.get("customers").getAsJsonArray();
        for(int i=0;i<Customers.size();i++) {
            List<Pair<String, Integer>> orderlist = new LinkedList<>();
            JsonArray Jorders = Customers.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray();
            for (int k = 0; k < Jorders.size(); k++) {
                JsonObject Jpair = Jorders.get(k).getAsJsonObject();
                Pair<String, Integer> pair = new Pair<>(Jpair.get("bookTitle").getAsString(), Jpair.get("tick").getAsInt());
                ((LinkedList<Pair<String, Integer>>) orderlist).push(pair);
            }

            JsonObject JC = Customers.get(i).getAsJsonObject();//Json Customer
            Customer Customer =new Customer(JC.get("id").getAsInt(),JC.get("name").getAsString(), JC.get("address").getAsString(), JC.get("distance").getAsInt(),
                    JC.get("creditCard").getAsJsonObject().get("number").getAsInt(),
                    JC.get("creditCard").getAsJsonObject().get("amount").getAsInt(),orderlist);

            APIService ApiService= new APIService(Customer);
            Thread ApiThread= new Thread(ApiService);
            ApiThread.start();


             }
             int sellingCount =Services.get("selling").getAsInt();
            for (int i=0;i<sellingCount;i++)
            {
                SellingService sellingService = new SellingService(i);
                Thread sellingThread=new Thread(sellingService);
                sellingThread.start();

            }
           //**********************************************************************TIME***************************************************

        Services.get("time").getAsJsonObject().get("speed").getAsInt();
        Services.get("time").getAsJsonObject().get("duration").getAsInt();
           TimeService timeService= new TimeService(Services.get("time").getAsJsonObject().get("speed").getAsInt(),Services.get("time").getAsJsonObject().get("duration").getAsInt());
           Thread timeThread = new Thread(timeService);
           timeThread.start();

    }



        }









