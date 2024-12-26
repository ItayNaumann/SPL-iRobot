package bgu.spl.mics.application.messages;

import java.util.concurrent.ConcurrentSkipListSet;

import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent{
    private ConcurrentSkipListSet<TrackedObject> tracked;
    
    public TrackedObjectsEvent(ConcurrentSkipListSet<TrackedObject> tracked){
        this.tracked = tracked;
    }

    public ConcurrentSkipListSet<TrackedObject> tracked(){
        return tracked;
    }
}