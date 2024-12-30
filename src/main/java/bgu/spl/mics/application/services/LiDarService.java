package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
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
 *
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    LiDarWorkerTracker LiDar;
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
        this.LiDar = liDarTracker;
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
            // time++;
            // if (time % LiDar.freq == 0) {
            //     for (TrackedObject o : LiDar.getLastTrackedObjects()) {
            //         sendEvent(TrackedObjectsEvent); // TODO need to implement this
            //     }
            //     time = 0;
            // }
            time++;
        });
        @SuppressWarnings
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent e) ->{
            ConcurrentHashMap<Integer, StampedCloudPoints> coords = getCoordsByTime();
            StampedDetectedObjects sdo = e.detectedObject();
            List<TrackedObject> tracked = new ArrayList<>();
            for (DetectedObject d : sdo.getDetectedObjects()){
                TrackedObject to = new TrackedObject(Integer.toString(d.id()), time, d.description(), StampedCloudPointsToCloudPoints(coords.get(d.id())));
                tracked.add(to);
                LiDar.getLastTrackedObjects().add(to);
            }
            try {Thread.sleep(LiDar.freq()*1000);}
            catch (InterruptedException e){ e.printStackTrace();}
            sendEvent(new TrackedObjectsEvent(tracked));
        });
    }
    // I assume that there could be muiltiple coords at one time
    private ConcurrentHashMap<Integer, StampedCloudPoints> getCoordsByTime(){
        ConcurrentHashMap<Integer, StampedCloudPoints> points = new ConcurrentHashMap<>();
        for (StampedCloudPoints scp : liDarDB.cloudPoints()){
            if (scp.timeStamp() == time){
                points.put(scp.id(), scp);
            }
        }
        return points;
    }

    private CloudPoint[] StampedCloudPointsToCloudPoints(StampedCloudPoints scp){
        CloudPoint[] output = new CloudPoint[scp.cloudPoints().length];
        for (int i = 0; i < output.length; i++){
            output[i] = new CloudPoint(scp.cloudPoints()[i][0], scp.cloudPoints()[i][1]);
        }
        return output;
    }
}
