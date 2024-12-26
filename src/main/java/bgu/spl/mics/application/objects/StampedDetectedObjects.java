package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private ConcurrentSkipListSet<DetectedObject> DetectedObjects;

    public StampedDetectedObjects(int time) {
        this.time = time;
        DetectedObjects = new ConcurrentSkipListSet<>();
    }

    public ConcurrentSkipListSet<DetectedObject> getDetectedObjects() {
        return this.DetectedObjects;
    }

    public int time(){
        return time;
    }

    public int getTime() {
        return this.time;
    }
}
