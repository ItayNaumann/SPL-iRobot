package bgu.spl.mics.application.services;

import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.messages.CrushedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    final Camera camera;
    int time;
    private ConcurrentLinkedQueue<StampedDetectedObjects> detectedQ;
    // private detectedQ;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Change_This_Name");
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
            StampedDetectedObjects sdo = getObjByTime();
            if (sdo != null) {
                detectedQ.add(sdo);
            }
            if (!detectedQ.isEmpty() && detectedQ.peek().time() == time - camera.freq()) {
                StampedDetectedObjects d;
                synchronized (detectedQ) {
                    d = detectedQ.remove();
                }

                for (DetectedObject detectedObject : d.getDetectedObjects()) {
                    if (detectedObject.id() == "ERROR") {
                        camera.setCurStatus(STATUS.ERROR);
                        detectedObject.description(); // TODO use it, it describes the error, needed in the json output
                        sendBroadcast(new CrushedBroadcast());
                        terminate();
                        return;
                    }
                }

                // You can choose to do something with b
                sendEvent(new DetectObjectsEvent(d));
            }
        });

        subscribeBroadcast(CrushedBroadcast.class, (CrushedBroadcast c) -> {
            camera.setCurStatus(STATUS.DOWN);
            terminate();
        });

    }

    private StampedDetectedObjects getObjByTime() {
        for (StampedDetectedObjects o : camera.detectedObjectsList()) {
            if (o.time() == time)
                return o;
        }
        return null;
    }

}
