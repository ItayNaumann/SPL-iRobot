package org.example;

import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Read from JSON
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("camera_data.json")){
            Type SDOList = new TypeToken<List<StampedDetectedObjects>>(){}.getType();
            List<StampedDetectedObjects> cameraDataList = gson.fromJson(reader, SDOList);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        try (FileReader reader = new FileReader("pose_data.json")){
            Type POSEList = new TypeToken<List<Pose>>(){}.getType();
            List<Pose> poseDataList = gson.fromJson(reader, POSEList);
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }
}