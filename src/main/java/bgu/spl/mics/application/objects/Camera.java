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

    public Camera(int id, int freq, STATUS status) {
        this.id = id;
        this.freq = freq;
        currStatus = status;
        detectObjectsList = new ArrayList<>();
    }

    public List<StampedDetectedObjects> detectedObjectsList(){
        return detectObjectsList;
    }

    public int freq(){
        return freq;
    }

}
