/* @Author: Sushant Amit Mathur. SJSU ID: 014489865*/

/* This is the data that we are sending over the network
*  and receiving too for now. We may change it later so
*  will add another class when need be.
* */

public class RoverData {
    int id;
    int time;
    int emr;
    int xray;
    int sunlight;

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public int getEmr() {
        return emr;
    }

    public int getXray() {
        return xray;
    }

    public int getSunlight() {
        return sunlight;
    }

    public RoverData(int id, int time, int emr, int xray, int sunlight){
        this.id = id;
        this.time = time;
        this.emr = emr;
        this.xray = xray;
        this.sunlight = sunlight;
    }
}
