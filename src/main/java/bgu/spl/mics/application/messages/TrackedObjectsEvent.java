package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<TrackedObject> {
    private TrackedObject tracked;

    public TrackedObjectsEvent(TrackedObject tracked) {
        this.tracked = tracked;
    }

    public TrackedObject tracked() {
        return tracked;
    }
}