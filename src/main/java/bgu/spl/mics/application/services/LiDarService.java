package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * <p>
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    LiDarWorkerTracker liDar;
    int time;
    String path = ""; // TODO: Write path into .getInstance
    LiDarDataBase liDarDB = LiDarDataBase.getInstance(path);

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to
     *                     process data.
     */
    public LiDarService(LiDarWorkerTracker // changed from LiDarTracker
                                liDarTracker) {
        super("Change_This_Name");
        this.liDar = liDarTracker;
        time = 0;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
            time = t.time;
            // if (time % LiDar.freq == 0) {
            // for (TrackedObject o : LiDar.getLastTrackedObjects()) {
            // sendEvent(TrackedObjectsEvent); // TODO need to implement this
            // }
            // time = 0;
            // }
        });

        //TODO: create a summarize of the output
        subscribeBroadcast(CrushedBroadcast.class, (CrushedBroadcast c) -> {
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            terminate();
        });

        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent ev) -> {
            ev.getHandledByID().compareAndSet(-1, liDar.id());
            if (ev.getHandledByID().get() == liDar.id()) {

                ConcurrentHashMap<String, StampedCloudPoints> coords = getCoordsByTime();
                StampedDetectedObjects sdo = ev.detectedObject();

                if (coords.get("ERROR") != null) {
                    sendBroadcast(new CrushedBroadcast(this, ""));
                    terminate();
                    return;
                }

                for (DetectedObject d : sdo.getDetectedObjects()) {
                    StampedCloudPoints cp = coords.get(d.id());
                    // TODO do something if LiDar.freq > Cam.freq

                    TrackedObject to = new TrackedObject(d.id(), time, d.description(),
                            StampedCloudPointsToCloudPoints(cp));

                    sendEvent(new TrackedObjectsEvent(to));

                    liDar.getLastTrackedObjects().add(to);
                }
                try {
                    Thread.sleep(liDar.freq() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // I assume that there could be muiltiple coords at one time
    private ConcurrentHashMap<String, StampedCloudPoints> getCoordsByTime() {
        ConcurrentHashMap<String, StampedCloudPoints> points = new ConcurrentHashMap<>();
        for (StampedCloudPoints scp : liDarDB.cloudPoints()) {
            if (scp.timeStamp() == time) {
                points.put(scp.id(), scp);
            }
        }
        return points;
    }

    private List<CloudPoint> StampedCloudPointsToCloudPoints(StampedCloudPoints scp) {
        LinkedList<CloudPoint> output = new LinkedList<>();
        for (int i = 0; i < scp.cloudPoints().length; i++) {
            output.add(new CloudPoint(scp.cloudPoints()[i][0], scp.cloudPoints()[i][1]));
        }
        return output;
    }
}
