package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class ConfigParse {
    public static class CameraConfig {
        private List<Camera> CamerasConfigurations;
        private String camera_datas_path;
        public CameraConfig() {
            CamerasConfigurations = new ArrayList<>();
            camera_datas_path = "";
        }
        public CameraConfig(List<Camera> CamerasConfigurations, String camera_datas_path) {
            this.CamerasConfigurations = CamerasConfigurations;
            this.camera_datas_path = camera_datas_path;
        }
    
        public List<Camera> getCamerasConfigurations() {
            return CamerasConfigurations;
        }
    
        public void setCamerasConfigurations(List<Camera> camerasConfigurations) {
            CamerasConfigurations = camerasConfigurations;
        }
    
        public String getCameraDatasPath() {
            return camera_datas_path;
        }
    
        public void setCameraDatasPath(String cameraDatasPath) {
            this.camera_datas_path = cameraDatasPath;
        }
    }
    public static class LidarConfig {
        private List<LiDarWorkerTracker> LidarConfigurations;
        private String lidars_data_path;

        public LidarConfig() {
            LidarConfigurations = new ArrayList<>();
            lidars_data_path = "";
        }
        public LidarConfig(List<LiDarWorkerTracker> LidarConfigurations, String lidars_data_path) {
            this.LidarConfigurations = LidarConfigurations;
            this.lidars_data_path = lidars_data_path;
        }
    
        public List<LiDarWorkerTracker> getLidarConfigurations() {
            return LidarConfigurations;
        }
    
        public void setLidarConfigurations(List<LiDarWorkerTracker> LidarConfigurations) {
            this.LidarConfigurations = LidarConfigurations;
        }
    
        public String getLidarsDataPath() {
            return lidars_data_path;
        }
    
        public void setLidarsDataPath(String lidars_data_path) {
            this.lidars_data_path = lidars_data_path;
        }
    }
    private String poseJsonFile;
    private int TickTime;
    private int Duration;
    private CameraConfig Cameras;
    private LidarConfig LiDarWorkers;

    public ConfigParse() {
        poseJsonFile = "";
        TickTime = 0;
        Duration = 0;
        Cameras = new CameraConfig();
        LiDarWorkers = new LidarConfig();
    }

    public ConfigParse(String poseJsonFile, int tickTime, int duration, CameraConfig Cameras, LidarConfig LidarWorkers) {
        this.poseJsonFile = poseJsonFile;
        TickTime = tickTime;
        Duration = duration;
        this.Cameras = Cameras;
        this.LiDarWorkers = LidarWorkers;
    }
    public String getPoseJsonFile() {
        return poseJsonFile;
    }
    
    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }
    
    public int getTickTime() {
        return TickTime;
    }
    
    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }
    
    public int getDuration() {
        return Duration;
    }
    
    public void setDuration(int duration) {
        Duration = duration;
    }
    
    public CameraConfig getCameras() {
        return Cameras;
    }
    
    public void setCameras(CameraConfig cameras) {
        Cameras = cameras;
    }
    
    public LidarConfig getLidars() {
        return LiDarWorkers;
    }
    
    public void setLidars(LidarConfig LiDarWorkers) {
        this.LiDarWorkers = LiDarWorkers;
    }
    
    
    
}
