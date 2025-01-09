package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {

    public final String error;
    public final MicroService crashed;

    public CrashedBroadcast(MicroService crushed, String error) {
        System.out.println("Crashed Broadcast: " + crushed.getName());
        this.error = error;
        this.crashed = crushed;
    }

}
