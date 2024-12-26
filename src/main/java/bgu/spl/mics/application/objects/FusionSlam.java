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
    private int latestPoseTime;

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

    }

    public void addLandMark(LandMark newLandMark) {
        for (LandMark landmark : landmarks) {
            if (landmark.equals(newLandMark)) {
                newLandMark.updateCoordinates(newLandMark.getCoordinates());
                return;
            }
        }
        landmarks.add(newLandMark);
    }

    public void addPose(Pose pose) {
        poses.add(pose);
        latestPoseTime = pose.time;
    }

    public int latestPoseTime() {
        return latestPoseTime;
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }
}
