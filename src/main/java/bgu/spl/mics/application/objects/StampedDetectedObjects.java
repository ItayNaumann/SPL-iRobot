package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private ConcurrentLinkedQueue<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time) {
        this.time = time;
        detectedObjects = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<DetectedObject> getDetectedObjects() {
        return this.detectedObjects;
    }

    public int time() {
        return time;
    }

    public String toString() {
        return time + " " + detectedObjects;
    }


}
