/* @Author: Sushant Amit Mathur. SJSU ID: 014489865*/
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.squareup.okhttp.*;
import org.bson.Document;

import java.io.IOException;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class Client2 {
    //Set the type of Media for the OkHTTP library
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");;
    private static MongoCollection<Document> rover2Col;
    private static MongoClient mongo;
    private static MongoDatabase database;
    private static int ID =2;

    //Sends the message with a POST request over HTTP
    public static void sendHttpRequest_update(int id, int time, int emr, int xray, int sunlight) throws IOException {
        Document document = new Document();
        document.put("roverid", ID);
        document.put("time", time);
        document.put("xray", xray);
        document.put("sunlight", sunlight);
        document.put("emr", emr);
        rover2Col.insertOne(document);

        RoverData rover2 = new RoverData(id,time,emr,xray,sunlight);
        String local = "http://localhost:8080/update";
        Gson gson = new Gson();
        String rover2Json = gson.toJson(rover2);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, rover2Json);
        Request request = new Request.Builder().url(local).post(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            ResponseMessage responseData = gson.fromJson(response.body().string(), ResponseMessage.class);
            System.out.println("The response for update is: "+ responseData.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Delete over HTTPS
    public static void sendHttpRequest_delete(int id, int time) throws IOException {
        //Delete from local db first
        BasicDBObject object = new BasicDBObject();
        object.put("time",time);
        rover2Col.deleteOne(object);
        //Send over the network
        String local = "http://localhost:8080/delete/" + Integer.toString(id) + "/"+ Integer.toString(time);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(local).delete().build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            //Print the rover ID and time as we got from the server
            System.out.println("The response ID is: " + response.code());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Modify over HTTP
    public static void sendHttpRequest_modify(int id, int time, int sunlight) throws IOException {
        //Modify in local db first
        Document document = (Document) rover2Col.find(eq("time", time)).first();
        document.put("sunlight", sunlight);
        rover2Col.replaceOne(Filters.eq("time", time), document);
        //Send over the network
        String local = "http://localhost:8080/modify/" + Integer.toString(id) +"/" + Integer.toString(time) +"/"+Integer.toString(sunlight);
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder().url(local).put(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            System.out.println("The response to modify for ID " +id + " time "+ time + " is: " +
                    " EMR: " + responseData.getEmr() + " Sunlight : "+ responseData.getSunlight() +
                    " Xray: " + responseData.getXray());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends get over get
    public static void sendHttpRequest_get(int id, int time) throws IOException {
        String local = "http://localhost:8080/get/"+Integer.toString(id) + "/" + Integer.toString(time);
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(local).get().build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            //Print the rover ID and time as we got from the server
            System.out.println("The response for get for ID " +id + " time "+ time + " is: " +
                    " EMR: " + responseData.getEmr() + " Sunlight : "+ responseData.getSunlight() +
                    " Xray: " + responseData.getXray());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws InterruptedException, IOException {
        mongo = MongoClients.create("mongodb://localhost:27017/admin");
        database = mongo.getDatabase("Client");
        rover2Col = database.getCollection("client2");

        sendHttpRequest_update(ID,128,234,315,234);
        sendHttpRequest_update(ID,256,235,136,11);
        sendHttpRequest_update(ID,512,236,757,453);
        sendHttpRequest_delete(ID,128);
        sendHttpRequest_modify(ID, 256,89);
        sendHttpRequest_get(ID,512);
    }
}

