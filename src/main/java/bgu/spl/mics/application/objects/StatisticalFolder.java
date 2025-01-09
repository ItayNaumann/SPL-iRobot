package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {
    private AtomicInteger systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;
    private Map<String, LandMark> landMarks;

    public StatisticalFolder(int systemRuntime, int objectsDetected, int trackedObjects, int landmarks) {
        this.systemRuntime = new AtomicInteger(systemRuntime);
        this.numDetectedObjects = new AtomicInteger(objectsDetected);
        this.numTrackedObjects = new AtomicInteger(trackedObjects);
        this.numLandmarks = new AtomicInteger(landmarks);
    }

    public void setLandMarks(List<LandMark> landMarkList) {
        this.landMarks = new HashMap<>();
        for (LandMark landMark : landMarkList) {
            landMarks.put(landMark.getId(), landMark);
        }
        this.numLandmarks.compareAndSet(this.numLandmarks.get(), landMarkList.size());
    }


    public int getLandMarkAmount() {

        return numLandmarks.get();
    }


    public int getNumSystemRuntime() {
        return systemRuntime.get();
    }

    public void setNumSystemRuntime(int systemRuntime) {
        if (systemRuntime > this.systemRuntime.get()) {
            this.systemRuntime.compareAndSet(this.systemRuntime.get(), systemRuntime);
        }
    }

    public int getNumObjectsDetected() {
        return numDetectedObjects.get();
    }

    public void setNumObjectsDetected(int numDetectedObjects) {
        this.numDetectedObjects.compareAndSet(this.numDetectedObjects.get(), numDetectedObjects);
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public void setNumTrackedObjects(int numTrackedObjects) {
        this.numTrackedObjects.compareAndSet(this.numTrackedObjects.get(), numTrackedObjects);
    }

    public void addSystemRuntime(int dRuntime) {
        this.systemRuntime.addAndGet(dRuntime);
    }

    public void addTrackedObjects(int dRuntime) {
        this.numTrackedObjects.addAndGet(dRuntime);
    }

    public void addObjectsDetected(int dRuntime) {
        this.numDetectedObjects.addAndGet(dRuntime);
    }

    public void addLandmark(int dRuntime) {
        this.numLandmarks.addAndGet(dRuntime);
    }

}
