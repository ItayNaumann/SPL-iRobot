package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int curTick;
    private STATUS curStatus;
    private List<Pose> timeStampedPoses;

    public GPSIMU(int curTick, STATUS status, List<Pose> poses) {
        this.curTick = curTick;
        this.curStatus = status;
        this.timeStampedPoses = poses;
    }

}
