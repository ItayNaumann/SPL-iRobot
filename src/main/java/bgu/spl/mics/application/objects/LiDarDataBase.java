package bgu.spl.mics.application.objects;

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
        // parse the points in the file
    }

    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }

        return instance;
    }

    public List<StampedCloudPoints> cloudPoints(){
        return cloudPoints;
    }
}
