package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.*;

import java.util.Iterator;
import  java.util.Map;


import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

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





    }

}
