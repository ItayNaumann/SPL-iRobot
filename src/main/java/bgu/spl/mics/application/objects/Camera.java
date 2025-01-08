package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    final private String id;
    final public int freq;
    private STATUS currStatus;
    private List<StampedDetectedObjects> detectObjectsList;

    public Camera(String id, int freq, STATUS status) {
        this.id = id;
        this.freq = freq;
        currStatus = status;
        detectObjectsList = new ArrayList<>();
    }

    public void setDetectObjectsList(List<StampedDetectedObjects> detectObjectsList) {
        this.detectObjectsList = detectObjectsList;
    }

    public List<StampedDetectedObjects> detectedObjectsList() {
        return detectObjectsList;
    }

    public int freq() {
        return freq;
    }

    public void setCurStatus(STATUS newStatus) {
        currStatus = newStatus;
    }

    public STATUS geStatus() {
        return currStatus;
    }

    public String getId() {
        return id;
    }

    public StampedDetectedObjects getObjByTime(int time) {
        for (StampedDetectedObjects o : detectObjectsList) {
            if (o.time() == time)
                return o;
        }
        return null;
    }
}
