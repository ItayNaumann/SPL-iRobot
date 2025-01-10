package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.TimeService;
import org.junit.jupiter.api.*;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

class MessageBusTest {

    MessageBusImpl bus;
    @BeforeEach
    void setUp() {
        bus = MessageBusImpl.getInstance();
    }
    @AfterEach
    void tearDown() {
        bus.cleanup();
    }
    @Test
    @DisplayName("Asserts that MicroServices that are subscribed to an event are saved on the MassageBus")
    void subscribeEventTest(){

        LiDarWorkerTracker l = new LiDarWorkerTracker(1,1,STATUS.UP);
        LiDarWorkerTracker l2 = new LiDarWorkerTracker(2,1,STATUS.UP);
        LiDarService ls = new LiDarService(l);
        LiDarService ls2 = new LiDarService(l2);
        bus.register(ls);
        bus.register(ls2);
        bus.subscribeEvent(DetectObjectsEvent.class,ls);
        bus.subscribeEvent(DetectObjectsEvent.class,ls2);

        ConcurrentLinkedQueue<MicroService> q = bus.messageSubMap().get(DetectObjectsEvent.class);
        ConcurrentLinkedQueue<MicroService> q2 = new ConcurrentLinkedQueue<>();
        q2.add(ls);
        q2.add(ls2);
        assertEquals(q2.remove(),q.remove());
        assertEquals(q2.remove(),q.remove());
        assertTrue(q.isEmpty() & q2.isEmpty());
    }

    @Test
    @DisplayName("Asserts that MicroServices that are subscribed to a broadcast are saved on the MassageBus")
    void subscribeBroadcastTest(){
        LiDarWorkerTracker l = new LiDarWorkerTracker(1,1,STATUS.UP);
        LiDarWorkerTracker l2 = new LiDarWorkerTracker(2,1,STATUS.UP);
        LiDarService ls = new LiDarService(l);
        LiDarService ls2 = new LiDarService(l2);
        bus.register(ls);
        bus.register(ls2);
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
    @DisplayName("Checks That all subscribed MicroServices received the broadcast")
    void sendBroadcastTest(){
        TickBroadcast tick = new TickBroadcast(0);

        MicroService m1 = new CameraService(new Camera(1,0,"Camera1"));
        MicroService m2 = new CameraService(new Camera(2,0,"Camera2"));

        MicroService m3 = new LiDarService(new LiDarWorkerTracker(1,0,STATUS.UP));
        MicroService m4 = new LiDarService(new LiDarWorkerTracker(2,0,STATUS.UP));

        MicroService m5 = new CameraService(new Camera(3,0,"Camera3"));

        // Subscribe to TickBroadcast
        bus.register(m1);
        bus.register(m2);
        bus.register(m3);
        bus.register(m4);
        bus.register(m5);
        m1.initialize();
        m2.initialize();
        m3.initialize();
        m4.initialize();


        bus.sendBroadcast(tick);

        assertFalse(bus.broadcastSubMap().get(TickBroadcast.class).isEmpty());
        assertNotNull(m1.getBroadcasts().get(tick.getClass().getName()));
        assertNotNull(m2.getBroadcasts().get(tick.getClass().getName()));
        assertNotNull(m3.getBroadcasts().get(tick.getClass().getName()));
        assertNotNull(m4.getBroadcasts().get(tick.getClass().getName()));
        assertTrue(m5.getBroadcasts().isEmpty());




    }


}