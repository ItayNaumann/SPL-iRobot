package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {
    private AtomicInteger systemRuntime;
    private AtomicInteger objectsDetected;
    private AtomicInteger trackedObjects;
    private AtomicInteger landmarks;

    public StatisticalFolder(int systemRuntime, int objectsDetected, int trackedObjects, int landmarks) {
        this.systemRuntime = new AtomicInteger(systemRuntime);
        this.objectsDetected = new AtomicInteger(objectsDetected);
        this.trackedObjects = new AtomicInteger(trackedObjects);
        this.landmarks = new AtomicInteger(landmarks);
    }

    public void setLandmarks(int landmarks) {

        this.landmarks.compareAndSet(this.landmarks.get(), landmarks);
    }

    public int getLandMarkAmount() {

        return landmarks.get();
    }


    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime.compareAndSet(this.systemRuntime.get(), systemRuntime);
    }

    public int getObjectsDetected() {
        return objectsDetected.get();
    }

    public void setObjectsDetected(int objectsDetected) {
        this.objectsDetected.compareAndSet(this.objectsDetected.get(), objectsDetected);
    }

    public int getTrackedObjects() {
        return trackedObjects.get();
    }

    public void setTrackedObjects(int trackedObjects) {
        this.trackedObjects.compareAndSet(this.trackedObjects.get(), trackedObjects);
    }

    public void addSystemRuntime(int dRuntime) {
        this.systemRuntime.addAndGet(dRuntime);
    }

    public void addTrackedObjects(int dRuntime) {
        this.trackedObjects.addAndGet(dRuntime);
    }

    public void addObjectsDetected(int dRuntime) {
        this.objectsDetected.addAndGet(dRuntime);
    }

    public void addLandmark(int dRuntime) {
        this.landmarks.addAndGet(dRuntime);
    }

}
