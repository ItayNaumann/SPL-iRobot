package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class DetectObjectsEvent implements Event<Boolean> {
    private final StampedDetectedObjects detectedObject;
private  AtomicInteger handledByID;

    public DetectObjectsEvent(StampedDetectedObjects detectedObject) {
        this.detectedObject = detectedObject;
        handledByID = new AtomicInteger(-1);
    }

    public StampedDetectedObjects detectedObject() {
        return detectedObject;
    }

    public AtomicInteger getHandledByID() {
        return handledByID;
    }
}
