package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.CloudPoint;

import java.util.List;

public class LastLiDarFrameBroadcast implements Broadcast {
    public final List<CloudPoint> mostRecentCloudPoints;

    public LastLiDarFrameBroadcast(List<CloudPoint> mostRecentCloudPoints) {
        System.out.println("created LastLiDarFrameBroadcast");
        this.mostRecentCloudPoints = mostRecentCloudPoints;
    }
}
