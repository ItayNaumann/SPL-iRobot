package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import javafx.scene.effect.Light.Distant;


/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    // Fields
    int TickTime;
    int Duration;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("Change_This_Name");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }


    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        Broadcast b = new TickBroadcast(Duration);     
        subscribeBroadcast(b.getClass(), c -> {
            try{
                if (Duration > 0){
                    sendBroadcast(b);
                    Thread.sleep(TickTime);
                    Duration--;
                }
                else{
                    terminate();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        });
    }
}
