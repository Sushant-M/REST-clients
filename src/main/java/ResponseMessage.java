/* The Response Message that will be sent to the
 *  Rover
 * */

public class ResponseMessage {
    private int id;
    private int time;
    ResponseMessage(int id, int time){
        this.id = id;
        this.time = time;
    }
    public int getId(){return id;}
}
