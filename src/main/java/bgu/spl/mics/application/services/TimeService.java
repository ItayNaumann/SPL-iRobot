package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.StartingTickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    // Fields
    int TickTime;
    int Duration;
    int timer;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.timer = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {
        TickBroadcast b = new StartingTickBroadcast(timer, TickTime);
        subscribeBroadcast(b.getClass(), c -> {
            try {
                timer++;
                if (Duration > timer) {
                    sendBroadcast(new TickBroadcast(timer));
                    Thread.sleep(TickTime * 1000L);
                } else {
                    terminate();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            terminate();
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            terminate();
        });

        sendBroadcast(b);

    }
}
