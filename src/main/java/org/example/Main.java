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
        List<Camera> cameras = config.getCameras().getCamerasConfigurations();
        HashMap<String, List<List<StampedDetectedObjects>>> cams = new HashMap<>();
        JsonObject jsonObject;
        try (FileReader reader = new FileReader(directoryPath + (config.getCameras().getCameraDatasPath()).substring(1))) {
            jsonObject = gson.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                List<List<StampedDetectedObjects>> myList;
                JsonArray jsonArray = jsonObject.getAsJsonArray(key);

                // Convert JsonArray to List<MyObject>
                Type listType = new TypeToken<List<List<StampedDetectedObjects>>>() {
                }.getType();
                myList = gson.fromJson(jsonArray, listType);
                cams.put(key, myList);
            }
            for (Camera c : cameras) {
                c.setDetectObjectsList(cams.get(c.getId()).get(0));
            }
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
        threads.add(new FusionSlamService(slam, directoryPath));
        threads.add(new PoseService(gpsimu));
        for (MicroService m : threads) {
            m.run();
        }
        timeService.run();


    }
}