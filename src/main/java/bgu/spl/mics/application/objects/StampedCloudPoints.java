package bgu.spl.mics.application.objects;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked
 * objects.
 */
public class StampedCloudPoints {
    final private String id;
    final private int time;
    final private double[][] cloudPoints;

    public StampedCloudPoints(String id, int timeStamp, double[][] cloudPoints) {
        this.id = id;
        this.time = timeStamp;
        this.cloudPoints = cloudPoints;
    }

    public int timeStamp() {
        return time;
    }

    public String id() {
        return id;
    }

    public double[][] cloudPoints() {
        return cloudPoints;
    }
}
