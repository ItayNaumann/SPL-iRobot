package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    String creationPath;
    int tickTime;
    int systemRuntime;
    int numDetectedObjects = 0;
    int numTrackedObjects = 0;
    ConcurrentLinkedQueue<MicroService> faultySensors;
    String error;
    ConcurrentLinkedQueue<List<CloudPoint>> lastLiDarsFrame;
    ConcurrentLinkedQueue<StampedDetectedObjects> lastCameraFrame;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam, String creationPath) {
        super("FusionSlamService");
        this.creationPath = creationPath;
        this.slam = fusionSlam;
        this.faultySensors = new ConcurrentLinkedQueue<MicroService>();
        this.lastLiDarsFrame = new ConcurrentLinkedQueue<>();
        this.lastCameraFrame = new ConcurrentLinkedQueue<>();
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

            numTrackedObjects++;
            numDetectedObjects++;

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
                //notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast msg) -> {
            if (msg.time == 0) {
                tickTime = 2 * ((StartingTickBroadcast) msg).TickTime;
            }
            systemRuntime = msg.time;
        });

        // TODO: create a summarize of the output
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
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

    private void createErrorJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ErrorOutputFile errorOutputFile = new ErrorOutputFile(systemRuntime, numDetectedObjects, numTrackedObjects,
                slam.getLandmarks().size(), error, faultySensors, lastLiDarsFrame, lastCameraFrame);

        try (FileWriter writer = new FileWriter(creationPath + "output.json")) {
            gson.toJson(errorOutputFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NormalOutputFile normalOutputFile = new NormalOutputFile(systemRuntime, numDetectedObjects, numTrackedObjects,
                slam.getLandmarks().size(), slam.getLandmarks());

        try (FileWriter writer = new FileWriter(creationPath + "output.json")) {
            gson.toJson(normalOutputFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ErrorOutputFile {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private String error;
        private List<Object> faultySensors;
        private List<List<Object>> lastLiDarsFrame;
        private List<Object> lastCameraFrame;

        public ErrorOutputFile(
                int systemRuntime, int numDetectedObjects, int numTrackedObjects,
                int numLandmarks, String error, ConcurrentLinkedQueue<MicroService> faultySensors,
                ConcurrentLinkedQueue<List<CloudPoint>> lastLiDarsFrame,
                ConcurrentLinkedQueue<StampedDetectedObjects> lastCameraFrame) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.error = error;
            this.faultySensors = Arrays.asList(faultySensors.toArray());

            this.lastLiDarsFrame = Arrays.asList((List<Object>[]) lastLiDarsFrame.toArray());

            this.lastCameraFrame = new LinkedList<>();
            for (StampedDetectedObjects StampedDetectedObjects : lastCameraFrame) {
                final List<Object> e = Arrays.asList(StampedDetectedObjects.getDetectedObjects().toArray());
                this.lastCameraFrame.add(e);
            }
        }
    }

    private class NormalOutputFile {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private List<LandMark> landmarks;

        public NormalOutputFile(int systemRuntime, int numDetectedObjects, int numTrackedObjects,
                                int numLandmarks, List<LandMark> landmarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landmarks = landmarks;
        }
    }

}
