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
    int tickTime;
    AtomicInteger inputsGot = new AtomicInteger(0);
    ConcurrentLinkedQueue<String> faultySensors;
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
        this.statisticalFolder = new StatisticalFolder(0, 0, 0, 0);
        this.faultySensors = new ConcurrentLinkedQueue<>();
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

            statisticalFolder.addObjectsDetected(1);
            statisticalFolder.addTrackedObjects(1);

            try {
                while (trackedObject.getTime() > slam.latestPoseTime()) {
                    synchronized (this) {
                        wait();
                    }
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
            statisticalFolder.setSystemRuntime(msg.time);
        });

        // TODO: create a summarize of the output
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            System.out.println("Crashed in fs");
            faultySensors.add(c.crashed.getName());
            error = c.error;
            inputsGot.incrementAndGet();

            tryCheckout();
        });

        subscribeBroadcast(LastLiDarFrameBroadcast.class, (LastLiDarFrameBroadcast c) -> {
            lastLiDarsFrame.add(c.mostRecentCloudPoints);
            inputsGot.incrementAndGet();
            tryCheckout();
        });

        subscribeBroadcast(LastCameraFrameBroadcast.class, (LastCameraFrameBroadcast c) -> {
            lastCameraFrame.add(c.lastFrame);
            inputsGot.incrementAndGet();
            tryCheckout();
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            System.out.println("Terminated in fs");
            createJson();
            try {
                Thread.sleep(tickTime * 1000L);
            } catch (InterruptedException ignored) {
            }


            terminate();
        });

    }

    private void createErrorJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ErrorOutputFile errorOutputFile = new ErrorOutputFile(statisticalFolder.getSystemRuntime(), statisticalFolder.getObjectsDetected(), statisticalFolder.getTrackedObjects(),
                slam.getLandmarks().size(), error, faultySensors, lastLiDarsFrame, lastCameraFrame);
        System.out.println("Err 1");
        try (FileWriter writer = new FileWriter(creationPath + "\\output.json")) {

            System.out.println("Err 2");
            gson.toJson(errorOutputFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void tryCheckout() {

        if (inputsGot.get() != slam.numOfCams + slam.numOfLiDars) {
            System.out.println("Tried checkout " + inputsGot.get() + " " + (slam.numOfCams + slam.numOfLiDars));
            return;
        }

        createErrorJson();
        try {
            Thread.sleep(tickTime * 1000L);
        } catch (InterruptedException ignored) {
        }

        terminate();

    }


    private void createJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NormalOutputFile normalOutputFile = new NormalOutputFile(statisticalFolder.getSystemRuntime(), statisticalFolder.getObjectsDetected(), statisticalFolder.getTrackedObjects(),
                slam.getLandmarks().size(), slam.getLandmarks());
        System.out.println("HERE 1");
        try (FileWriter writer = new FileWriter(creationPath + "\\output.json")) {
            System.out.println("HERE 2");
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
        private List<List<CloudPoint>> lastLiDarsFrame;
        private List<Object> lastCameraFrame;

        public ErrorOutputFile(
                int systemRuntime, int numDetectedObjects, int numTrackedObjects,
                int numLandmarks, String error, ConcurrentLinkedQueue<String> faultySensors,
                ConcurrentLinkedQueue<List<CloudPoint>> lastLiDarsFrame,
                ConcurrentLinkedQueue<StampedDetectedObjects> lastCameraFrame) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.error = error;
            this.faultySensors = Arrays.asList(faultySensors.toArray());


            this.lastLiDarsFrame = new LinkedList<>();
            this.lastLiDarsFrame.addAll(lastLiDarsFrame);


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
