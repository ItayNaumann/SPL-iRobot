package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<Boolean> {
    private StampedDetectedObjects detectedObject;

    public DetectObjectsEvent(StampedDetectedObjects detectedObject) {
        this.detectedObject = detectedObject;
    }

    public StampedDetectedObjects detectedObject() {
        return detectedObject;
    }
}
