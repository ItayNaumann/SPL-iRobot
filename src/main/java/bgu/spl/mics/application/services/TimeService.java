package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

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
        // Need to make sure it runs smoothly otherwise we could wait longer than expected
        try{
            wait();
            while(Duration > 0){
                try{
                    Thread.sleep(TickTime);
                    sendBroadcast(new TickBroadcast());
                    Duration--;
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            notifyAll();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
