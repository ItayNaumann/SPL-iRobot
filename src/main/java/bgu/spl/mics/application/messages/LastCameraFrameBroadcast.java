package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class LastCameraFrameBroadcast implements Broadcast {
    public final StampedDetectedObjects lastFrame;

    public LastCameraFrameBroadcast(StampedDetectedObjects lf) {
        lastFrame = lf;
    }
}
