package bgu.spl.mics.application.Messages;


import bgu.spl.mics.Broadcast;

public class TickFinalBroadcast implements Broadcast {

    private int finalTick;
    public TickFinalBroadcast(int tick){
        this.finalTick=tick;
    }

    public int getFinalTick() {
        return this.finalTick;
    }
}
