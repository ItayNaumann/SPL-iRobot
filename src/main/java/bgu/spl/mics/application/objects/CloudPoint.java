package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the
 * LiDAR.
 * These points are used to generate a point cloud representing objects in the
 * environment.
 */
public class CloudPoint {
    private double x;
    private double y;

    public CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void average(CloudPoint other) {
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
    }

    public void rotate(double yaw) {
        final float sin = (float) Math.sin(yaw);
        final float cos = (float) Math.cos(yaw);

        final double newX = x * cos - y * sin;
        final double newY = x * sin + y * cos;

        x = newX;
        y = newY;

    }

    public void add(double otherX, double otherY) {
        x = x + otherX;
        y = y + otherY;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
}
