/* @Author: Sushant Amit Mathur. SJSU ID: 014489865*/
import com.google.gson.Gson;
import com.squareup.okhttp.*;
import java.io.IOException;
import java.util.Date;

public class Client1 {
    //Set the type of Media for the OkHTTP library
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");;

    //Sends the message with a POST request over HTTP
    public static void sendHttpRequest_update(int id, int time, int emr, int xray, int sunlight) throws IOException {
        //RoverData rover1 = new RoverData(1,4566,234,654,96);
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
            System.out.println(responseData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Delete over HTTPS
    public static void sendHttpRequest_delete(int id, int time) throws IOException {
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
        String local = "http://localhost:8080/modify/" + Integer.toString(id) +"/" + Integer.toString(time) +"/"+Integer.toString(sunlight);
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder().url(local).put(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            ResponseMessage responseData = gson.fromJson(response.body().string(), ResponseMessage.class);
            //Print the rover ID and time as we got from the server
            System.out.println(responseData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sends Delete over Delete
    public static void sendHttpRequest_get(int id, int time) throws IOException {
        String local = "http://localhost:8080/get/"+Integer.toString(id) + "/" + Integer.toString(time);
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(local).get().build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            //Print the rover ID and time as we got from the server
            System.out.println(responseData.getEmr());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String args[]) throws InterruptedException, IOException {
        sendHttpRequest_update(1,128,234,315,234);
        sendHttpRequest_update(1,256,235,136,11);
        sendHttpRequest_update(1,512,236,757,453);
        sendHttpRequest_delete(1,128);
        sendHttpRequest_modify(1, 256,89);
        sendHttpRequest_get(1,512);
    }
}
