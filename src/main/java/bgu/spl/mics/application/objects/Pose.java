package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate
 * system.
 */
public class Pose {
    // Why was final?
    public int time;
    public double x;
    public double y;
    public double yaw;

    public Pose() {
        x = 0;
        y = 0;
        yaw = 0;
        time = 0;
    }

    public Pose(double x, double y, double yaw, int time) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getYaw() {
        return this.yaw;
    }


}
