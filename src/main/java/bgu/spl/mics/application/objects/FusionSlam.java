package bgu.spl.mics.application.objects;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
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

    private final List<LandMark> landmarks;
    private final List<Pose> poses;
    private Pose latestPose;
    public int numOfCams = 0;
    public int numOfLiDars = 0;

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
        LinkedList<CloudPoint> newPoints = new LinkedList<>();
        CloudPoint newPoint;
        for (CloudPoint point : trackedObject.getCoordinates()) {
            newPoint = new CloudPoint(point);

            newPoint.rotate(yaw);
            newPoint.add(p.x, p.y);
            newPoints.add(newPoint);
        }


        return new LandMark(trackedObject.getID(), trackedObject.getDescription(), newPoints);

    }

    public LandMark addLandMark(LandMark newLandMark) {
        for (LandMark landmark : landmarks) {
            if (landmark.equals(newLandMark)) {
                landmark.updateCoordinates(newLandMark.getCoordinates());
                return landmark;
            }
        }
        synchronized (landmarks) {
            landmarks.add(newLandMark);
        }
        return newLandMark;
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

    /**
     * For tests
     */
    public void cleanup(){
        landmarks.clear();
        poses.clear();
        latestPose = null;
        numOfCams = 0;
        numOfLiDars = 0;
    }
}
