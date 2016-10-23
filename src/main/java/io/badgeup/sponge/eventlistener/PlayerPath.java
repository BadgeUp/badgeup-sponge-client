package io.badgeup.sponge.eventlistener;

import com.flowpowered.math.vector.Vector3i;
import io.badgeup.sponge.JSONSerializable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerPath implements JSONSerializable {

    private double distance;
    private List<Vector3i> points;

    public PlayerPath() {
        this.distance = 0;
        this.points = new ArrayList<>();
    }

    public void addPoint(Vector3i point) {
        if (this.points.size() == 0) {
            this.points.add(point);
        } else {
            Vector3i lastPoint = this.points.get(this.points.size() - 1);
            if (!point.equals(lastPoint)) {
                this.points.add(point);
            }
            this.distance += lastPoint.distance(point);
        }
    }

    public double getDistance() {
        return this.distance;
    }

    public int size() {
        return this.points.size();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("distance", this.distance);

        JSONArray pathArray = new JSONArray();
        for (Vector3i point : this.points) {
            pathArray.put(new JSONObject().put("x", point.getX()).put("y", point.getY()).put("z", point.getZ()));
        }
        obj.put("path", pathArray);
        return obj;
    }

}
