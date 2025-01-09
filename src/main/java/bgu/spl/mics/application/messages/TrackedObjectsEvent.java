package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<TrackedObject> {
    private final TrackedObject tracked;
    public final int time;

    public TrackedObjectsEvent(int time, TrackedObject tracked) {
        this.time = time;
        this.tracked = tracked;
    }

    public TrackedObject tracked() {
        return tracked;
    }
}