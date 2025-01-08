package bgu.spl.mics.application.services;

import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * <p>
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    final Camera camera;
    int time;
    private final ConcurrentLinkedQueue<StampedDetectedObjects> detectedQ;
    private StampedDetectedObjects lastDetectedObjects;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("name"); //TODO change name
        this.camera = camera;
        time = 0;
        detectedQ = new ConcurrentLinkedQueue<>();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, c -> {
            time = c.time;
            StampedDetectedObjects sdo = camera.getObjByTime(time);
            if (sdo != null) {
                detectedQ.add(sdo);
            }
            if (!detectedQ.isEmpty() && detectedQ.peek().time() == time - camera.freq()) {
                StampedDetectedObjects d;
                synchronized (detectedQ) {
                    d = detectedQ.remove();
                }

                for (DetectedObject detectedObject : d.getDetectedObjects()) {
                    if (detectedObject.id().equals("ERROR")) {
                        camera.setCurStatus(STATUS.ERROR);
                        sendBroadcast(new CrashedBroadcast(this, detectedObject.description()));
                        terminate();
                        return;
                    }
                }

                lastDetectedObjects = d;

                // You can choose to do something with b
                sendEvent(new DetectObjectsEvent(d));
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            camera.setCurStatus(STATUS.DOWN);
            sendBroadcast(new LastCameraFrameBroadcast(lastDetectedObjects));
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            camera.setCurStatus(STATUS.DOWN);
            terminate();
        });

    }


}
