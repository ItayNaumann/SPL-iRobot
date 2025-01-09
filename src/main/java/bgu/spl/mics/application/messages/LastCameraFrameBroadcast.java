package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class LastCameraFrameBroadcast implements Broadcast {
    public final String name;
    public final StampedDetectedObjects lastFrame;

    public LastCameraFrameBroadcast(String name, StampedDetectedObjects lf) {
        System.out.println("create last camera frame + " + name);
        this.name = name;
        lastFrame = lf;
    }
}
