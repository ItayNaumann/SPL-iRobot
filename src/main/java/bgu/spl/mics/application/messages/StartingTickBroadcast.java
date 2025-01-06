package bgu.spl.mics.application.messages;

public class StartingTickBroadcast extends TickBroadcast {
    final public int TickTime;

    public StartingTickBroadcast(int time, int TickTime) {
        super(time);
        this.TickTime = TickTime;
    }
}
