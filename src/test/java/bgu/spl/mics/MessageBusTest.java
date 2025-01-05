package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.TimeService;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

class MessageBusTest {



    @Test
    void subscribeEventTest(){
        MessageBusImpl bus = MessageBusImpl.getInstance();
        LiDarWorkerTracker l = new LiDarWorkerTracker(1,1,STATUS.UP);
        LiDarWorkerTracker l2 = new LiDarWorkerTracker(2,1,STATUS.UP);
        LiDarService ls = new LiDarService(l);
        LiDarService ls2 = new LiDarService(l2);
        bus.subscribeEvent(DetectObjectsEvent.class,ls);
        bus.subscribeEvent(DetectObjectsEvent.class,ls2);

        ConcurrentLinkedQueue<MicroService> q = bus.eventSubsMap().get(DetectObjectsEvent.class);
        ConcurrentLinkedQueue<MicroService> q2 = new ConcurrentLinkedQueue<>();
        q2.add(ls);
        q2.add(ls2);
        assertEquals(q2.remove(),q.remove());
        assertEquals(q2.remove(),q.remove());
        assertTrue(q.isEmpty() & q2.isEmpty());
    }

    @Test
    void subscribeBroadcastTest(){
        MessageBusImpl bus = MessageBusImpl.getInstance();
        LiDarWorkerTracker l = new LiDarWorkerTracker(1,1,STATUS.UP);
        LiDarWorkerTracker l2 = new LiDarWorkerTracker(2,1,STATUS.UP);
        LiDarService ls = new LiDarService(l);
        LiDarService ls2 = new LiDarService(l2);
        bus.subscribeBroadcast(TickBroadcast.class,ls);
        bus.subscribeBroadcast(TickBroadcast.class,ls2);

        ConcurrentLinkedQueue<MicroService> list = bus.broadcastSubMap().get(TickBroadcast.class);
        ConcurrentLinkedQueue<MicroService> list2 = new ConcurrentLinkedQueue<>();
        list2.add(ls);
        list2.add(ls2);

        Iterator<MicroService> iter = list.iterator();
        Iterator<MicroService> iter2 = list2.iterator();

        while (iter.hasNext() & iter2.hasNext()){
            assertEquals(iter.next(),iter2.next());
        }
    }
    @Test
    void completeTest(){
        MessageBusImpl bus = MessageBusImpl.getInstance();

        LiDarWorkerTracker l = new LiDarWorkerTracker(1,1,STATUS.UP);
        LiDarWorkerTracker l2 = new LiDarWorkerTracker(2,1,STATUS.UP);
        LiDarService ls = new LiDarService(l);
        LiDarService ls2 = new LiDarService(l2);
        bus.subscribeBroadcast(TickBroadcast.class,ls);
        bus.subscribeBroadcast(TickBroadcast.class,ls2);

        StampedDetectedObjects sdo = new StampedDetectedObjects(1);
        sdo.getDetectedObjects().add(new DetectedObject("1","wall"));

        StampedDetectedObjects sdo1 = new StampedDetectedObjects(2);
        sdo1.getDetectedObjects().add(new DetectedObject("2","toy"));

        Event e = new DetectObjectsEvent(sdo);
        Event e1 = new DetectObjectsEvent(sdo1);
        bus.sendEvent(e);
        bus.sendEvent(e1);

        bus.complete(e,1);
        bus.complete(e1,-1);

        assertEquals(e,new DetectObjectsEvent(sdo).setResult(1));
        assertEquals(e1,new DetectObjectsEvent(sdo1).setResult(-1));

        assertTrue(bus.eventSubsMap().isEmpty());
    }
}