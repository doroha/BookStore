package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private Integer tick;

    public TickBroadcast(Integer t){
        this.tick=t;
    }
    public Integer getTick(){return this.tick;}


}
