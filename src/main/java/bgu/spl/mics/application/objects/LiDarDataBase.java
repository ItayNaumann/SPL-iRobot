package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for
 * tracked objects.
 */
public class LiDarDataBase {

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private static LiDarDataBase instance = null;
    private List<StampedCloudPoints> cloudPoints;

    private LiDarDataBase(String filePath) {
        cloudPoints = new ArrayList<>();
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            Type SDPList = new TypeToken<List<StampedCloudPoints>>(){}.getType();
            cloudPoints = gson.fromJson(reader, SDPList);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }

        return instance;
    }

    public List<StampedCloudPoints> cloudPoints() {
        return cloudPoints;
    }

    public StampedCloudPoints getCloudPoints(int time, int freq) {
        for (StampedCloudPoints points : cloudPoints) {
            if (points.timeStamp() == (time + freq)) {
                return points;
            }
        }

        return null;
    }
}
