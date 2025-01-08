package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    GPSIMU gpsImu;
    int time;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Change_This_Name");
        this.gpsImu = gpsimu;
        time = 0;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast msg) -> {
            time = msg.time;
            gpsImu.setCurTick(time);
            Pose pose = gpsImu.getPose();

//            probably not needed
//            if (pose == null) {
//                gpsImu.setCurStatus(STATUS.ERROR);
//                sendBroadcast(new CrushedBroadcast());
//                return;
//            }

            sendEvent(new PoseEvent(pose));
        });


        //TODO: create a summarize of the output
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            gpsImu.setCurStatus(STATUS.DOWN);
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            gpsImu.setCurStatus(STATUS.DOWN);
            terminate();
        });
    }
}
