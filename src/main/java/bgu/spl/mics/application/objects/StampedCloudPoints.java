package bgu.spl.mics.application.objects;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked
 * objects.
 */
public class StampedCloudPoints {
    final private int id;
    final private int timeStamp;
    final private double[][] cloudPoints;

    public StampedCloudPoints(int id, int timeStamp, double[][] cloudPoints) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.cloudPoints = cloudPoints;
    }
}
