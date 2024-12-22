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
    private static FusionSlam slam = null;

    private List<LandMark> landmarks;
    private List<Pose> poses;

    private FusionSlam() {
        landmarks = new ArrayList<>();
        poses = new ArrayList<>();
    }

    // Singleton instance holder
    public static FusionSlam getInstance() {
        if (slam == null) {
            slam = new FusionSlam();
        }

        return slam;
    }

    private static class FusionSlamHolder {
        // TODO: Implement singleton instance logic.
    }
}
