package bgu.spl.mics.application.Messages;

public class GetVehicleEvent {

    private int license;
    private int speed;
    public GetVehicleEvent(int license, int speed){
        this.license=license;
        this.speed= speed;

    }
    public  int getLicense(){
        return license;
    }
    public int getSpeed(){return speed;}
}
