package org.example;

import bgu.spl.mics.application.objects.ConfigParse;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.*;
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
        List<StampedDetectedObjects> cameraDataList;
        try (FileReader reader = new FileReader(directoryPath + (config.getCameras().getCameraDatasPath()).substring(1))) {
            Type SDOList = new TypeToken<List<StampedDetectedObjects>>() {
            }.getType();
            cameraDataList = gson.fromJson(reader, SDOList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Pose> poseDataList = new ArrayList<>();
        try (FileReader reader = new FileReader(directoryPath + (config.getLidars().getLidarsDataPath()).substring(1))) {
            Type POSEList = new TypeToken<List<Pose>>() {
            }.getType();
            poseDataList = gson.fromJson(reader, POSEList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LiDarDataBase liDarDB = LiDarDataBase.getInstance(directoryPath + (config.getLidars().getLidarsDataPath()).substring(1));
        List<Camera> cameras = config.getCameras().getCamerasConfigurations();
        List<LiDarWorkerTracker> lidars = config.getLidars().getLidarConfigurations();
        int tickTime = config.getTickTime();
        int duration = config.getDuration();
        StatisticalFolder statistics = new StatisticalFolder(0, 0, 0, 0);
        TimeService timeService = new TimeService(tickTime, duration);
        MessageBus bus = MessageBusImpl.getInstance();
        FusionSlam slam = FusionSlam.getInstance();
        GPSIMU gpsimu = new GPSIMU(0, STATUS.UP, poseDataList);
        List<MicroService> threads = new LinkedList<>();
        for (Camera c : cameras) {
            threads.add(new CameraService(c));
        }
        for (LiDarWorkerTracker lwt : lidars) {
            threads.add(new LiDarService(lwt, liDarDB));
        }
        threads.add(new FusionSlamService(slam));
        threads.add(new PoseService(gpsimu));
        for (MicroService m : threads) {
            m.run();
        }
        timeService.run();


    }
}