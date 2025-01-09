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
    private int id;
    private int freq;
    private STATUS currStatus;
    private List<TrackedObject> lastTrackedObjects;

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

    public STATUS status() {return currStatus;}

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }
    public void setStatus(STATUS status) {
        currStatus = status;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LiDarWorkerTracker that = (LiDarWorkerTracker) other;
        return id == that.id && freq == that.freq && currStatus == that.currStatus;
    }


}
