package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.LinkedList;
import java.util.List;

public class LastLiDarFrameBroadcast implements Broadcast {
    public final LinkedList<TrackedObject> mostRecentCloudPoints;
    public final String name;

    public LastLiDarFrameBroadcast(String name, LinkedList<TrackedObject> mostRecentCloudPoints) {
        System.out.println("created LastLiDarFrameBroadcast + " + name);
        this.mostRecentCloudPoints = mostRecentCloudPoints;
        this.name = name;
    }
}
