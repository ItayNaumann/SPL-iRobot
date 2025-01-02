package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    public final int time;

    public TickBroadcast(int time) {
        this.time = time;
    }

}
