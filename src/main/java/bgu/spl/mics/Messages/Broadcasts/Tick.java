package bgu.spl.mics.Messages.Broadcasts;

import bgu.spl.mics.Broadcast;

public class Tick implements Broadcast {
    private int tickNumber;
    private boolean last;
    public Tick(int tickNumber, boolean last){
        this.tickNumber = tickNumber;
        this.last = last;
    }

    public boolean getLast(){
        return last;
    }

    public int getTickNumber() {
        return tickNumber;
    }

}
