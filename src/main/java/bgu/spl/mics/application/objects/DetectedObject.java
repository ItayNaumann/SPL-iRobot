package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {
    private final int id;
    private final String description;

    public DetectedObject(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int id(){
        return id;
    }

    public String description(){
        return description;
    }
}
