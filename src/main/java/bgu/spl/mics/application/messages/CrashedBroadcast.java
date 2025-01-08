package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {

    public final String error;
    public final MicroService crushed;

    public CrashedBroadcast(MicroService crushed, String error) {
    this.error = error;
    this.crushed = crushed;
    }

}
