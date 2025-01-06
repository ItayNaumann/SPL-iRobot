package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;
import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * <p>
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    FusionSlam slam;
    int tickTime;
    List<MicroService> faultySensors;
    String error;
    List<List<CloudPoint>> lastLiDarsFrame;
    List<StampedDetectedObjects> lastCameraFrame;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("Change_This_Name");
        this.slam = fusionSlam;
        this.faultySensors = new LinkedList<>();
        this.lastLiDarsFrame = new LinkedList<>();
        this.lastCameraFrame = new LinkedList<>();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent msg) -> {
            TrackedObject trackedObject = msg.tracked();
            try {
                while (trackedObject.getTime() > slam.latestPoseTime()) {
                    wait();
                }
                LandMark newLandMark = slam.calcLandMark(trackedObject);
                slam.addLandMark(newLandMark);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        subscribeEvent(PoseEvent.class, (PoseEvent ev) -> {
            try {
                slam.addPose(ev.getPose());
                notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast msg) -> {
            if (msg.time == 0) {
                tickTime = 2 * ((StartingTickBroadcast) msg).TickTime;
            }
        });

        //TODO: create a summarize of the output
        subscribeBroadcast(CrushedBroadcast.class, (CrushedBroadcast c) -> {
            faultySensors.add(c.crushed);
            error = c.error;
            try {
                Thread.sleep(tickTime * 1000L);
            } catch (InterruptedException ignored) {
            }
            createErrorJson();
            terminate();
        });

        subscribeBroadcast(LastLiDarFrameBroadcast.class, (LastLiDarFrameBroadcast c) -> {
            lastLiDarsFrame.add(c.mostRecentCloudPoints);
        });

        subscribeBroadcast(LastCameraFrameBroadcast.class, (LastCameraFrameBroadcast c) -> {
            lastCameraFrame.add(c.lastFrame);
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            try {
                Thread.sleep(tickTime * 1000L);
            } catch (InterruptedException ignored) {
            }
            createJson();
            terminate();
        });

    }
}
