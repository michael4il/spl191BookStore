package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Json.Input;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import  java.util.Map;


import java.io.FileReader;
import java.io.IOException;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {//testing
    public static void main(String[] args) throws IOException{
        JsonObject jsoninput ;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try (FileReader fileReader = new FileReader(args[0]))
        { jsoninput=parser.parse(fileReader).getAsJsonObject();}
        catch (IOException e ) {throw e;}
        System.out.println(jsoninput.size());//inventory,resources,services(including customer)
        Map<String, String> map = new Gson().fromJson(jsoninput.toString(),Map.class);
     //   JsonObject Inventory = jsoninput.getAsJsonObject("initialInventory");
        System.out.println(map.keySet());
     //   Map<String, String> mapI = new Gson().fromJson(Inventory.toString(),Map.class);
       // System.out.println(mapI);

// need to use Json element?











    }
}
