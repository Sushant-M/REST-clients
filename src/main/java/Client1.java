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
//import com.mongodb.client.MongoClients;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class Client1 {
    //Set the type of Media for the OkHTTP library
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");;
    private static MongoCollection<Document> rover1Col;
    private static MongoClient mongo;
    private static MongoDatabase database;
    private static int ID =1;

    //Sends the message with a POST request over HTTP
    public static void sendHttpRequest_update(int id, int time, int emr, int xray, int sunlight, MongoCollection<Document> rover1Col) throws IOException {
        //Add to db
        Document document = new Document();
        document.put("roverid", ID);
        document.put("time", time);
        document.put("xray", xray);
        document.put("sunlight", sunlight);
        document.put("emr", emr);

        //Opens up possibility of adding new factors.
        if(sunlight > 250 && xray < 200){
            document.put("energy factor", (sunlight*3) - 65);
        }

        rover1Col.insertOne(document);

        //Send the req.
        RoverData rover1 = new RoverData(id,time,emr,xray,sunlight);
        String local = "http://localhost:8080/update";
        Gson gson = new Gson();
        String rover1Json = gson.toJson(rover1);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, rover1Json);
        Request request = new Request.Builder().url(local).post(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            ResponseMessage responseData = gson.fromJson(response.body().string(), ResponseMessage.class);
            System.out.println("Response for update is: " + responseData.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Delete over HTTPS
    public static void sendHttpRequest_delete(int id, int time, MongoCollection<Document> rover1Col) throws IOException {
        //Delete from local db first
        BasicDBObject object = new BasicDBObject();
        object.put("time",time);
        rover1Col.deleteOne(object);
        //Send the req
        String local = "http://localhost:8080/delete/" + Integer.toString(id) + "/"+ Integer.toString(time);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(local).delete().build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            //Print the rover ID and time as we got from the server
            System.out.println("The response for delete is: " + response.code());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Modify over HTTP
    public static void sendHttpRequest_modify(int id, int time, int sunlight, MongoCollection<Document> rover1Col) throws IOException {
        //Modify in local db first
        Document document = (Document) rover1Col.find(eq("time", time)).first();
        document.put("sunlight", sunlight);
        rover1Col.replaceOne(Filters.eq("time", time), document);

        String local = "http://localhost:8080/modify/" + Integer.toString(id) +"/" + Integer.toString(time) +"/"+Integer.toString(sunlight);
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder().url(local).put(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            //Print the rover ID and time as we got from the server
            System.out.println("The response to modify for ID " +id + " time "+ time + " is: " +
                    " EMR: " + responseData.getEmr() + " Sunlight : "+ responseData.getSunlight() +
                    " Xray: " + responseData.getXray());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Delete over Delete
    public static void sendHttpRequest_get(int id, int time, MongoCollection<Document> rover1Col) throws IOException {
        //No need to change the db here. Just request from server.
        String local = "http://localhost:8080/get/"+Integer.toString(id) + "/" + Integer.toString(time);
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(local).get().build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            //Print the rover ID and time as we got from the server
            System.out.println("The response to get for ID " +id + " time "+ time + " is: " +
                    " EMR: " + responseData.getEmr() + " Sunlight : "+ responseData.getSunlight() +
                    " Xray: " + responseData.getXray());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String args[]) throws InterruptedException, IOException {
        mongo = MongoClients.create("mongodb://localhost:27017/admin");
        database = mongo.getDatabase("Client");
        rover1Col = database.getCollection("client1");

        sendHttpRequest_update(ID,128,234,315,234, rover1Col);
        sendHttpRequest_update(ID,256,235,136,11 , rover1Col);
        sendHttpRequest_update(ID,512,236,120,453, rover1Col);
        sendHttpRequest_delete(ID,128, rover1Col);
        sendHttpRequest_modify(ID, 256,89,rover1Col);
        sendHttpRequest_get(ID,256,rover1Col);
    }
}
