package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent<T> implements Event<T>{
    private StampedDetectedObjects detectedObject;

    public DetectObjectsEvent(StampedDetectedObjects detectedObject){
        this.detectedObject = detectedObject;
    }

    public StampedDetectedObjects detectedObject(){
        return detectedObject;
    }
}
