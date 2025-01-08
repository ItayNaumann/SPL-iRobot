package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
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

        FusionSlam fs = FusionSlam.getInstance();

        fs.addPose(pose1);
        fs.addPose(pose2);
        fs.addPose(pose3);

        LandMark l1 = new LandMark(to1.getID(), to1.getDescription(), list1);
        LandMark l2 = new LandMark(to2.getID(), to2.getDescription(), list2);
        LandMark l3 = new LandMark(to3.getID(), to3.getDescription(), list3);

        assertTrue(fs.calcLandMark(to1).equals(l1) & l1.getCoordinates().equals(list1));
        assertTrue(fs.calcLandMark(to2).equals(l2) & l2.getCoordinates().equals(list2));
        assertTrue(fs.calcLandMark(to3).equals(l3) & l3.getCoordinates().equals(list3));
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

        LinkedList<CloudPoint> points4 = new LinkedList<>();
        points4.addLast(new CloudPoint(p10.x() - pose3.x, p10.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        points4.addLast(new CloudPoint(p11.x() - pose3.x, p11.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        points4.addLast(new CloudPoint(p12.x() - pose3.x, p12.y() - pose3.y));
        points4.getLast().rotate(-pose3.yaw);
        TrackedObject to4 = new TrackedObject("4", 6, "wall3", points4);

        FusionSlam fs = FusionSlam.getInstance();

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
    }
}
