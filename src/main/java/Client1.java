/* @Author: Sushant Amit Mathur. SJSU ID: 014489865*/
import com.google.gson.Gson;
import com.squareup.okhttp.*;
import java.io.IOException;
import java.util.Date;

public class Client1 {
    //Set the type of Media for the OkHTTP library
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");;

    //Sends the message with a POST request over HTTP
    public static void sendHttpRequest() throws IOException{
        //Get system time, diving by 1000 to be able to store in int.
        int time_1 = (int) (new Date().getTime()/1000);
        //Initialise new message with the client's ID and time
        RoverData rover1 = new RoverData(1,time_1);
        /* I am using the OkHTTP library to send data
           here I create a POST request and send it over,
           parse the response and print the values onto System.out
         */
        String local = "http://localhost:8080/update";
        Gson gson = new Gson();
        String rover1Json = gson.toJson(rover1);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, rover1Json);
        Request request = new Request.Builder().url(local).post(body).build();
        //Try getting the response and print exception if occurs
        try{ Response response = okHttpClient.newCall(request).execute();
            RoverData responseData = gson.fromJson(response.body().string(), RoverData.class);
            //Print the rover ID and time as we got from the server
            System.out.println("The response ID is: " + responseData.id+ " The time is: "+
                    new Date(((long)responseData.time)*1000L));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        /* Run periodically 10 times with 4 seconds delay
           between each call. */
        int i = 0;
        while(i<10) {
            Thread.sleep(4000);
            try {
                sendHttpRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
