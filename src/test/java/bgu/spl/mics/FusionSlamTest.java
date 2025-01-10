package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests 0-3 are not that important, tests 4 and 5 are.
 * Please take that into consideration.
 */
class FusionSlamTest {

    FusionSlam fs;

    @BeforeEach
    void setUp() {
        fs = FusionSlam.getInstance();
    }

    @AfterEach
    void tearDown() {
        fs.cleanup();
    }

    @Test
    void calcLandMark0() {

        Pose p1 = new Pose(-4.953, 1.3106, 144.06, 4);
        Pose p2 = new Pose(1.2, 5.1, 85.95, 12);
        List t1l = new ArrayList<>();
        t1l.add(new CloudPoint(3.0451, -0.38171));
        t1l.add(new CloudPoint(3.0637, -0.17392));
        TrackedObject t1 = new TrackedObject("Wall_3", 4, "", t1l);

        List t2l = new ArrayList<>();
        t2l.add(new CloudPoint(3.1, -0.4));
        t2l.add(new CloudPoint(3.2, -0.2));
        TrackedObject t2 = new TrackedObject("Wall_3", 12, "", t2l);

        FusionSlam sl = FusionSlam.getInstance();

        sl.addPose(p1);
        sl.addPose(p2);
        sl.addLandMark(sl.calcLandMark(t1));

        List<CloudPoint> lmp = new ArrayList<>();
        lmp.add(new CloudPoint(-2.6882128166564954, 5.78546831239456));
        lmp.add(new CloudPoint(-2.8529412210562732, 5.763747954999182));
        LandMark l = new LandMark("Wall_3", "", lmp);
        assertEquals(sl.addLandMark(sl.calcLandMark(t2)), l);
        System.out.println("Test 0 passed");

    }

    @Test
    void calcLandMarkTest1() {

        Pose pose1 = new Pose(1.0, 2.5, 0.0, 2);
        Pose pose2 = new Pose(1.0, 3.5, 45.0, 4);
        Pose pose3 = new Pose(1.5, 5.0, 90, 6);

        List<CloudPoint> list1 = new ArrayList<>();
        List<CloudPoint> list2 = new ArrayList<>();
        List<CloudPoint> list3 = new ArrayList<>();

        List<CloudPoint> list1lid = new ArrayList<>();
        List<CloudPoint> list2lid = new ArrayList<>();
        List<CloudPoint> list3lid = new ArrayList<>();

        CloudPoint p1 = new CloudPoint(2.0, 2.0);
        CloudPoint p2 = new CloudPoint(2.0, 2.5);
        CloudPoint p3 = new CloudPoint(2.0, 3.0);
        list1.add(p1);
        list1.add(p2);
        list1.add(p3);

        LinkedList<CloudPoint> points1 = new LinkedList<>();
        points1.addLast(new CloudPoint(p3.x() - pose1.x, p3.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p2.x() - pose1.x, p2.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p1.x() - pose1.x, p1.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        TrackedObject to1 = new TrackedObject("1", 2, "wall1", points1);

        CloudPoint p4 = new CloudPoint(1.5, 4.0);
        CloudPoint p5 = new CloudPoint(1.25, 4.25);
        CloudPoint p6 = new CloudPoint(1.75, 3.75);
        list2.add(p4);
        list2.add(p5);
        list2.add(p6);

        LinkedList<CloudPoint> points2 = new LinkedList<>();
        points2.addLast(new CloudPoint(p6.x() - pose2.x, p6.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        points2.addLast(new CloudPoint(p5.x() - pose2.x, p5.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        points2.addLast(new CloudPoint(p4.x() - pose2.x, p4.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        TrackedObject to2 = new TrackedObject("2", 4, "toy", points2);

        CloudPoint p7 = new CloudPoint(2.0, 6.0);
        CloudPoint p8 = new CloudPoint(1.5, 6.0);
        CloudPoint p9 = new CloudPoint(1.0, 6.0);
        list3.add(p7);
        list3.add(p8);
        list3.add(p9);

        LinkedList<CloudPoint> points3 = new LinkedList<>();
        points3.addLast(new CloudPoint(p9.x() - pose3.x, p9.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        points3.addLast(new CloudPoint(p8.x() - pose3.x, p8.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        points3.addLast(new CloudPoint(p7.x() - pose3.x, p7.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        TrackedObject to3 = new TrackedObject("3", 6, "wall2", points3);


        fs.addPose(pose1);
        fs.addPose(pose2);
        fs.addPose(pose3);

        LandMark l1 = new LandMark(to1.getID(), to1.getDescription(), list1);
        LandMark l2 = new LandMark(to2.getID(), to2.getDescription(), list2);
        LandMark l3 = new LandMark(to3.getID(), to3.getDescription(), list3);

        assertTrue(fs.calcLandMark(to1).equals(l1) & l1.getCoordinates().equals(list1));
        assertTrue(fs.calcLandMark(to2).equals(l2) & l2.getCoordinates().equals(list2));
        assertTrue(fs.calcLandMark(to3).equals(l3) & l3.getCoordinates().equals(list3));
        System.out.println("Test 1 passed");
    }

    @Test
    void calcLandMarkTest2() {

        Pose pose1 = new Pose(1.0, 2.5, 0.0, 2);
        Pose pose2 = new Pose(1.0, 3.5, 135.0, 4);
        Pose pose3 = new Pose(1.5, 5.0, 45, 6);

        List<CloudPoint> list1 = new ArrayList<>();
        List<CloudPoint> list2 = new ArrayList<>();
        List<CloudPoint> list3 = new ArrayList<>();
        List<CloudPoint> list4 = new ArrayList<>();

        List<CloudPoint> list1lid = new ArrayList<>();
        List<CloudPoint> list2lid = new ArrayList<>();
        List<CloudPoint> list3lid = new ArrayList<>();
        List<CloudPoint> list4lid = new ArrayList<>();

        CloudPoint p1 = new CloudPoint(2.0, 2.25);
        CloudPoint p2 = new CloudPoint(2.0, 2.5);
        CloudPoint p3 = new CloudPoint(2.0, 2.75);
        list1.add(p1);
        list1.add(p2);
        list1.add(p3);

        LinkedList<CloudPoint> points1 = new LinkedList<>();
        points1.addLast(new CloudPoint(p3.x() - pose1.x, p3.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p2.x() - pose1.x, p2.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p1.x() - pose1.x, p1.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        TrackedObject to1 = new TrackedObject("1", 2, "wall1", points1);

        CloudPoint p4 = new CloudPoint(0.65, 3.65);
        CloudPoint p5 = new CloudPoint(0.75, 3.75);
        CloudPoint p6 = new CloudPoint(0.85, 3.85);
        list2.add(p4);
        list2.add(p5);
        list2.add(p6);

        LinkedList<CloudPoint> points2 = new LinkedList<>();
        points2.addLast(new CloudPoint(p6.x() - pose2.x, p6.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        points2.addLast(new CloudPoint(p5.x() - pose2.x, p5.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        points2.addLast(new CloudPoint(p4.x() - pose2.x, p4.y() - pose2.y));
        points2.getLast().rotate(-pose2.yaw);
        TrackedObject to2 = new TrackedObject("2", 4, "toy", points2);

        CloudPoint p7 = new CloudPoint(2.0, 6.0);
        CloudPoint p8 = new CloudPoint(2.0, 5.75);
        CloudPoint p9 = new CloudPoint(2.0, 5.5);
        list3.add(p7);
        list3.add(p8);
        list3.add(p9);

        LinkedList<CloudPoint> points3 = new LinkedList<>();
        points3.addLast(new CloudPoint(p9.x() - pose3.x, p9.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        points3.addLast(new CloudPoint(p8.x() - pose3.x, p8.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        points3.addLast(new CloudPoint(p7.x() - pose3.x, p7.y() - pose3.y));
        points3.getLast().rotate(-pose3.yaw);
        TrackedObject to3 = new TrackedObject("3", 6, "wall2", points3);

        CloudPoint p10 = new CloudPoint(1.5, 6.0);
        CloudPoint p11 = new CloudPoint(1.75, 6.0);
        CloudPoint p12 = new CloudPoint(2.0, 6.0);
        list4.add(p10);
        list4.add(p11);
        list4.add(p12);

        LinkedList<CloudPoint> points5 = new LinkedList<>();

        TrackedObject to5 = new TrackedObject("4", 6, "wall3", points3);

        CloudPoint p13 = new CloudPoint(-1.5, 6.0);
        CloudPoint p14 = new CloudPoint(-1.75, 6.0);
        CloudPoint p15 = new CloudPoint(-2.0, 6.0);

        LinkedList<CloudPoint> points4 = new LinkedList<>();
        points4.addLast(new CloudPoint(p10.x() - pose3.x, p10.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        points4.addLast(new CloudPoint(p11.x() - pose3.x, p11.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        points4.addLast(new CloudPoint(p12.x() - pose3.x, p12.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        TrackedObject to4 = new TrackedObject("4", 6, "wall3", points4);


        fs.addPose(pose1);
        fs.addPose(pose2);
        fs.addPose(pose3);

        LandMark l1 = new LandMark(to1.getID(), to1.getDescription(), list1);
        LandMark l2 = new LandMark(to2.getID(), to2.getDescription(), list2);
        LandMark l3 = new LandMark(to3.getID(), to3.getDescription(), list3);
        LandMark l4 = new LandMark(to4.getID(), to4.getDescription(), list4);

        assertTrue(fs.calcLandMark(to1).equals(l1) & l1.getCoordinates().equals(list1));
        assertTrue(fs.calcLandMark(to2).equals(l2) & l2.getCoordinates().equals(list2));
        assertTrue(fs.calcLandMark(to3).equals(l3) & l3.getCoordinates().equals(list3));
        assertTrue(fs.calcLandMark(to4).equals(l4) & l4.getCoordinates().equals(list4));
        System.out.println("Test 2 passed");
    }

    @Test
    void calcLandMarkTest3() {

        Pose pose1 = new Pose(1.0, 2.5, 0.0, 2);
        Pose pose2 = new Pose(1.0, 3.5, 0.0, 4);

        List<CloudPoint> list1 = new ArrayList<>();
        List<CloudPoint> list2 = new ArrayList<>();

        CloudPoint p1 = new CloudPoint(2.0, 2.0);
        CloudPoint p2 = new CloudPoint(2.0, 2.5);
        CloudPoint p3 = new CloudPoint(2.0, 3.0);
        list1.add(p1);
        list1.add(p2);
        list1.add(p3);

        LinkedList<CloudPoint> points1 = new LinkedList<>();
        points1.addLast(new CloudPoint(p3.x() - pose1.x, p3.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p2.x() - pose1.x, p2.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        points1.addLast(new CloudPoint(p1.x() - pose1.x, p1.y() - pose1.y));
        points1.getLast().rotate(-pose1.yaw);
        TrackedObject to1 = new TrackedObject("1", 2, "wall1", points1);

        CloudPoint p4 = new CloudPoint(2.0, 2.0);
        CloudPoint p5 = new CloudPoint(2.0, 2.5);
        CloudPoint p6 = new CloudPoint(2.0, 3.0);
        list2.add(p4);
        list2.add(p5);
        list2.add(p6);

        LinkedList<CloudPoint> points2 = new LinkedList<>();
        CloudPoint prevFirst = points1.get(0);
        CloudPoint curFirst = new CloudPoint(p6.x() - pose2.x, (p6.y() - pose2.y));
        curFirst.rotate(-pose2.yaw);
        points2.addLast(new CloudPoint((curFirst.x() + prevFirst.x()) / 2, (curFirst.y() + prevFirst.y()) / 2));
        CloudPoint prevSecond = points1.get(1);
        CloudPoint curSecond = new CloudPoint(p5.x() - pose2.x, (p5.y() - pose2.y));
        curSecond.rotate(-pose2.yaw);
        points2.addLast(new CloudPoint((curSecond.x() + prevSecond.x()) / 2, (curSecond.y() + prevSecond.y()) / 2));
        CloudPoint prevThird = points1.get(2);
        CloudPoint curThird = new CloudPoint(p4.x() - pose2.x, (p4.y() - pose2.y));
        curThird.rotate(-pose2.yaw);
        points2.addLast(new CloudPoint((curThird.x() + prevThird.x()) / 2, (curThird.y() + prevThird.y()) / 2));
        TrackedObject to2 = new TrackedObject("1", 4, "wall1", points2);


        fs.addPose(pose1);
        fs.addPose(pose2);

        LandMark l1 = new LandMark(to1.getID(), to1.getDescription(), list1);
        LandMark l2 = new LandMark(to2.getID(), to2.getDescription(), list2);

        assertTrue(fs.calcLandMark(to1).equals(l1));
        assertTrue(fs.calcLandMark(to2).equals(l2));
        System.out.println("Test 3 passed");
    }

    @Test
    @DisplayName("LandMark appears once")
    void calcLandMarkTest4() {

        Pose pose1 = new Pose( -2.366,  0.9327, -28.08, 7);
        fs.addPose(pose1);

        TrackedObject to = new TrackedObject("Door", 7, "Door", new LinkedList<>());
        CloudPoint p1 = new CloudPoint(0.5, -2.1);
        CloudPoint p2 = new CloudPoint(0.8, -2.3);
        to.getCoordinates().add(p1);
        to.getCoordinates().add(p2);

        LandMark doorLM = new LandMark("Door", "Door", new LinkedList<>());
        CloudPoint p3 = new CloudPoint(-2.913332578606659, -1.1554635639732926);
        CloudPoint p4 = new CloudPoint(-2.7427859966862367, -1.4731329886827864);
        doorLM.getCoordinates().add(p3);
        doorLM.getCoordinates().add(p4);
        assertEquals(fs.calcLandMark(to), doorLM);
        assertTrue(Math.abs(fs.calcLandMark(to).getCoordinates().get(0).x()-p3.x()) < 0.01);
        assertTrue(Math.abs(fs.calcLandMark(to).getCoordinates().get(0).y()-p3.y()) < 0.01);
        assertTrue(Math.abs(fs.calcLandMark(to).getCoordinates().get(1).x()-p4.x()) < 0.01);
        assertTrue(Math.abs(fs.calcLandMark(to).getCoordinates().get(1).y()-p4.y()) < 0.01);
        System.out.println("Test 4 passed");
    }

    @Test

     @DisplayName("Landmark appears twice")

    void calcLandMarkTest5() {
        Pose pose1 = new Pose( -3.16,  3.1058, -28.08, 6);
        Pose pose2 = new Pose( 5.5,  7.2, 148.91, 16);
        fs.addPose(pose1);
        fs.addPose(pose2);

        TrackedObject to6 = new TrackedObject("Wall_4", 6, "Wall", new LinkedList<>());
        TrackedObject to16 = new TrackedObject("Wall_4", 16, "Wall", new LinkedList<>());

        CloudPoint p1 = new CloudPoint(-2.5367, -3.3341);
        CloudPoint p2 = new CloudPoint(1.7926, -3.6804);
        CloudPoint p3 = new CloudPoint(-2.5, -3.3);
        CloudPoint p4 = new CloudPoint(1.8, -3.6);

        to6.getCoordinates().add(p1);
        to6.getCoordinates().add(p2);

        to16.getCoordinates().add(p3);
        to16.getCoordinates().add(p4);

        LandMark wall_4 = new LandMark("Wall_4", "Wall", new LinkedList<>());
        CloudPoint p5 = new CloudPoint(1.1887387639977982, 5.046603301251042);
        CloudPoint p6 = new CloudPoint(1.2533775541582042, 5.113604111414717);

        wall_4.getCoordinates().add(p5);
        wall_4.getCoordinates().add(p6);

        fs.addLandMark(fs.calcLandMark(to6));
        fs.addLandMark(fs.calcLandMark(to16));

        List<CloudPoint> landMarkPoints = fs.getLandmarks().get(0).getCoordinates();

       assertEquals(fs.getLandmarks().get(0), wall_4);
       assertEquals(fs.getLandmarks().size(), 1);
        assertTrue(Math.abs(landMarkPoints.get(0).x()-p5.x()) < 0.01);
        assertTrue(Math.abs(landMarkPoints.get(0).y()-p5.y()) < 0.01);
        assertTrue(Math.abs(landMarkPoints.get(1).x()-p6.x()) < 0.01);
        assertTrue(Math.abs(landMarkPoints.get(1).y()-p6.y()) < 0.01);
        System.out.println("Test 5 passed");
    }


}
