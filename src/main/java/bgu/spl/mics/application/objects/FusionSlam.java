package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {

    private List<LandMark> landmarks;
    private List<Pose> poses;
    private Pose latestPose;

    private FusionSlam() {
        landmarks = new ArrayList<>();
        poses = new ArrayList<>();
    }

    // Singleton instance holder
    private static class SlamHolder {
        private static FusionSlam slam = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return SlamHolder.slam;
    }

    public LandMark calcLandMark(TrackedObject trackedObject) {
        Pose p = findPoseAtTime(trackedObject.getTime());
        double yaw = degToRad(p.yaw);
        for (CloudPoint point : trackedObject.getCoordinates()) {
            synchronized (point) {
                point.rotate(yaw);
                point.add(p.x, p.y);
            }
        }

        return new LandMark(trackedObject.getID(), trackedObject.getDescription(), trackedObject.getCoordinates());

    }

    public void addLandMark(LandMark newLandMark) {
        for (LandMark landmark : landmarks) {
            if (landmark.equals(newLandMark)) {
                newLandMark.updateCoordinates(newLandMark.getCoordinates());
                return;
            }
        }
        synchronized (landmarks) {
            landmarks.add(newLandMark);
        }
    }

    public void addPose(Pose pose) {
        poses.add(pose);
        latestPose = pose;
    }

    private Pose findPoseAtTime(int time) {
        if (time == latestPose.time)
            return latestPose;
        for (Pose p : poses) {
            if (p.time == time)
                return p;
        }
        return null;
    }

    public int latestPoseTime() {
        return latestPose.time;
    }

    private double degToRad(double theta) {
        return Math.toRadians(theta);
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }
}
