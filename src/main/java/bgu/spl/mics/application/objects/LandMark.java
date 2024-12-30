package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String description;
    private final List<CloudPoint> coordinates;

    public LandMark(String id, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public void updateCoordinates(List<CloudPoint> newCoordinates) {
        for (int i = 0; i < coordinates.size(); i++) {
            CloudPoint curr = coordinates.get(i);
            CloudPoint other = newCoordinates.get(i);
            synchronized (curr) {
                curr.average(other);
            }
        }

    }

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LandMark) {
            return id.equals(((LandMark) obj).getId()) && description.equals(((LandMark) obj).getDescription());
        }
        return false;
    }
}
