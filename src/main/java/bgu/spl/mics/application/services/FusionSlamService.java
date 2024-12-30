package bgu.spl.mics.application.services;

import java.util.concurrent.ConcurrentSkipListSet;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.example.messages.ExampleEvent;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    FusionSlam slam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("Change_This_Name");
        this.slam = fusionSlam;
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
                slam.addLandMark(slam.calcLandMark(trackedObject));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        subscribeEvent(PoseEvent.class, (PoseEvent event) -> {
            PoseEvent pose = event;
            try {
                slam.addPose(pose.getPose());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast msg) -> {
            time++;
        });

    }
}
