package bgu.spl.mics.application.Messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private Integer currentTick;

    public TickBroadcast(Integer t){
        this.currentTick=t;
    }

    public Integer getTick(){return this.currentTick;}
}
