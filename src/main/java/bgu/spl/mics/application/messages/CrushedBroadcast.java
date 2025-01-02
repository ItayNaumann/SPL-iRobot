package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrushedBroadcast implements Broadcast {

    public final String error;
    public final MicroService crushed;

    public CrushedBroadcast(MicroService crushed, String error) {
    this.error = error;
    this.crushed = crushed;
    }

}
