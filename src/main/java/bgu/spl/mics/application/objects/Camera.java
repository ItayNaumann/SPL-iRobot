package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    final private int id;
    final public int freq;
    private STATUS currStatus;
    private List<StampedDetectedObjects> detectObjectsList;
    private String camera_key;

    public Camera(int id, int freq, String camera_key) {
        this.id = id;
        this.freq = freq;
        currStatus = STATUS.UP;
        detectObjectsList = new ArrayList<>();
        this.camera_key = camera_key;
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

    public int getId() {
        return id;
    }

    public String getCameraKey() {return camera_key;}

    public void setCameraKey(String camera_key) {this.camera_key = camera_key;}

    public StampedDetectedObjects getObjByTime(int time) {
        for (StampedDetectedObjects o : detectObjectsList) {
            if (o.time() == time)
                return o;
        }
        return null;
    }
}
