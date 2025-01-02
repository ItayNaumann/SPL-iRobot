package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    final private int id;
    final public int freq;
    final private STATUS currStatus;
    final private List<TrackedObject> lastTrackedObjects;

    public LiDarWorkerTracker(int id, int freq, STATUS status) {
        this.id = id;
        this.freq = freq;
        currStatus = status;
        lastTrackedObjects = new ArrayList<>();
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public int freq() {
        return freq;
    }

    public int id() {
        return id;
    }
}
