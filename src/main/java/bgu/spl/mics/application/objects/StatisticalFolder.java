package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {
    private int systemRuntime;
    private int objectsDetected;
    private int trackedObjects;
    private int landmarks;

    public StatisticalFolder(int systemRuntime, int objectsDetected, int trackedObjects, int landmarks) {
        this.systemRuntime = systemRuntime;
        this.objectsDetected = objectsDetected;
        this.trackedObjects = trackedObjects;
        this.landmarks = landmarks;
    }

    public void addLandMark() {
        landmarks++;
    }

    public int getLandMarkAmount() {
        return landmarks;
    }
}
