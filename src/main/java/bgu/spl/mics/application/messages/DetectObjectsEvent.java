package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class DetectObjectsEvent implements Event<Boolean> {
    private final StampedDetectedObjects detectedObject;
    private  AtomicInteger handledByID;
    private boolean result;

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

    public void setResult(boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public boolean equals(Object o) {
        if (o instanceof DetectObjectsEvent) {
            DetectObjectsEvent e = (DetectObjectsEvent) o;
            return e.detectedObject.equals(detectedObject)
                    & result == e.result
                    & handledByID.get() == e.handledByID.get();
        }
        return false;
    }
}
