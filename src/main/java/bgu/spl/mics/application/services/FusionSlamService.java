package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
    StatisticalFolder statisticalFolder;
    AtomicInteger inputsGot = new AtomicInteger(0);
    ConcurrentLinkedQueue<String> faultySensors;
    String error;
    final Map<String, List<TrackedObject>> lastLiDarsFrame;
    final Map<String, StampedDetectedObjects> lastCameraFrame;

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
        this.statisticalFolder = new StatisticalFolder(0, 0, 0, 0);
        this.faultySensors = new ConcurrentLinkedQueue<>();
        this.lastLiDarsFrame = new ConcurrentHashMap<>();
        this.lastCameraFrame = new ConcurrentHashMap<>();
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

            if (trackedObject.getTime() > slam.latestPoseTime()) {
                sendEvent(msg);
                return;
            }

            statisticalFolder.addObjectsDetected(1);
            statisticalFolder.addTrackedObjects(1);

            LandMark newLandMark = slam.calcLandMark(trackedObject);
            slam.addLandMark(newLandMark);

            statisticalFolder.setNumSystemRuntime(msg.time);

        });

        subscribeEvent(PoseEvent.class, (PoseEvent ev) -> {
            try {
                slam.addPose(ev.getPose());
                statisticalFolder.setNumSystemRuntime(ev.getPose().time);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // TODO: create a summarize of the output
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            System.out.println("Crashed in fs");
            faultySensors.add(c.crashed.getName());
            error = c.error;
        });

        subscribeBroadcast(LastLiDarFrameBroadcast.class, (LastLiDarFrameBroadcast c) -> {
            System.out.println("Got lidar frame");
            lastLiDarsFrame.put(c.name, c.mostRecentCloudPoints);
            tryCheckout();
        });

        subscribeBroadcast(LastCameraFrameBroadcast.class, (LastCameraFrameBroadcast c) -> {
            System.out.println("Got cam frame");
            lastCameraFrame.put(c.name, c.lastFrame);
            tryCheckout();
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            System.out.println("Terminated in fs");
            createJson();

            terminate();
        });

    }

    private void createErrorJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        statisticalFolder.setLandMarks(slam.getLandmarks());
        ErrorOutputFile errorOutputFile = new ErrorOutputFile(error, faultySensors, lastLiDarsFrame, lastCameraFrame,
                slam.getPoses(), statisticalFolder);
        try (FileWriter writer = new FileWriter(creationPath + "/OutputError.json")) {
            gson.toJson(errorOutputFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryCheckout() {
        inputsGot.incrementAndGet();
        if (inputsGot.get() != slam.numOfCams + slam.numOfLiDars) {
            System.out.println("Tried checkout " + inputsGot.get() + " " + (slam.numOfCams + slam.numOfLiDars));
            return;
        }
        System.out.println("Got here");
        createErrorJson();
        System.out.println("Got here 1");

        terminate();

    }

    private void createJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        statisticalFolder.setLandMarks(slam.getLandmarks());
        NormalOutputFile normalOutputFile = new NormalOutputFile(statisticalFolder, slam.getLandmarks());
        try (FileWriter writer = new FileWriter(creationPath + "/output_file.json")) {
            gson.toJson(normalOutputFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ErrorOutputFile {

        private String error;
        private List<Object> faultySensors;
        private Map<String, StampedDetectedObjects> lastCamerasFrame;
        private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
        private List<Pose> poses;
        private StatisticalFolder statistics;

        public ErrorOutputFile(
                String error, ConcurrentLinkedQueue<String> faultySensors,
                Map<String, List<TrackedObject>> lastLiDarsFrame,
                Map<String, StampedDetectedObjects> lastCameraFrame, List<Pose> poses,
                StatisticalFolder statisticalFolder) {
            this.error = error;
            this.faultySensors = Arrays.asList(faultySensors.toArray());

            this.lastLiDarWorkerTrackersFrame = lastLiDarsFrame;

            this.poses = poses;

            this.lastCamerasFrame = lastCameraFrame;

            this.statistics = statisticalFolder;
        }
    }

    private class NormalOutputFile {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private Map<String, LandMark> landMarks;

        public NormalOutputFile(StatisticalFolder statisticalFolder, List<LandMark> landmarks) {
            this.systemRuntime = statisticalFolder.getNumSystemRuntime();
            this.numDetectedObjects = statisticalFolder.getNumObjectsDetected();
            this.numTrackedObjects = statisticalFolder.getNumTrackedObjects();
            this.numLandmarks = statisticalFolder.getLandMarkAmount();

            this.landMarks = new HashMap<>();
            for (LandMark lm : landmarks) {
                this.landMarks.put(lm.getId(), lm);
            }
        }
    }

}
