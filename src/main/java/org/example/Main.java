package org.example;

import bgu.spl.mics.application.objects.ConfigParse;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.*;
import com.sun.org.apache.xerces.internal.xs.StringList;
import sun.awt.image.ImageWatched;

public class Main {
    public static void main(String[] args) {
        // Read from JSON
        Gson gson = new Gson();

        // Config parse
        ConfigParse config = new ConfigParse();
        File file = new File(args[0]);
        String directoryPath = file.getParent();

        try (FileReader reader = new FileReader(args[0])) {
            config = gson.fromJson(reader, ConfigParse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("config parsed");

        Map<String, List<StampedDetectedObjects>> camMap = new HashMap<>();
        try (FileReader reader = new FileReader(directoryPath + (config.getCameras().getCameraDatasPath()).substring(1))) {
            Type cameraMap = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {
            }.getType();
            camMap = gson.fromJson(reader, cameraMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("cams parsed");

        List<Pose> poseDataList = new ArrayList<>();
        try (FileReader reader = new FileReader(directoryPath + (config.getPoseJsonFile()).substring(1))) {
            Type POSEList = new TypeToken<List<Pose>>() {
            }.getType();
            poseDataList = gson.fromJson(reader, POSEList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("pose data parsed");

        LiDarDataBase liDarDB = LiDarDataBase.getInstance(directoryPath + (config.getLidars().getLidarsDataPath()).substring(1));

        System.out.println("lidar db parsed");

        List<LiDarWorkerTracker> lidars = config.getLidars().getLidarConfigurations();
        List<Camera> cameras = config.getCameras().getCamerasConfigurations();

        int tickTime = config.getTickTime();
        int duration = config.getDuration();
        StatisticalFolder statistics = new StatisticalFolder(0, 0, 0, 0);
        TimeService timeService = new TimeService(tickTime, duration);
        MessageBus bus = MessageBusImpl.getInstance();
        FusionSlam slam = FusionSlam.getInstance();
        GPSIMU gpsimu = new GPSIMU(0, STATUS.UP, poseDataList);
        List<MicroService> threads = new ArrayList<>();

        for (Camera c : cameras) {
            c.setCurStatus(STATUS.UP);
            c.setDetectObjectsList(camMap.get(c.getCameraKey()));
            threads.add(new CameraService(c));
        }
        for (LiDarWorkerTracker lwt : lidars) {
            lwt.setStatus(STATUS.UP);
            lwt.setLastTrackedObjects(new ArrayList<>());
            threads.add(new LiDarService(lwt, liDarDB));
        }
        System.out.println("threads added");

        threads.add(new FusionSlamService(slam, directoryPath));
        threads.add(new PoseService(gpsimu));
        List<Thread> Threads = new ArrayList<>();
        for (MicroService m : threads) {
            Threads.add(new Thread(m));
            Threads.get(Threads.size() - 1).start();
        }
        System.out.println("threads started");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Thread timer = new Thread(timeService);
        timer.start();
        System.out.println("timer started");
        try {
            timer.join();
            for (Thread t : Threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}