package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent<T> implements Event<T>{
    private List<TrackedObject> tracked;
    
    public TrackedObjectsEvent(List<TrackedObject> tracked){
        this.tracked = tracked;
    }

    public List<TrackedObject> tracked(){
        return tracked;
    }
}