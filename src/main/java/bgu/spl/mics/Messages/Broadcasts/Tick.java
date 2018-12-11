package bgu.spl.mics.Messages.Broadcasts;

import bgu.spl.mics.Broadcast;

public class Tick implements Broadcast {
    private int tickNumber;

    public Tick(int tickNumber){
        this.tickNumber = tickNumber;
    }

    public int getTickNumber() {
        return tickNumber;
    }

}
