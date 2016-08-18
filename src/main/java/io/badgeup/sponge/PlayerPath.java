package io.badgeup.sponge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flowpowered.math.vector.Vector3i;

public class PlayerPath implements JSONSerializable {

	private double distance;
	private List<Vector3i> points;

	public PlayerPath() {
		this.distance = 0;
		this.points = new ArrayList<>();
	}

	public void addPoint(Vector3i point) {
		if (points.size() == 0) {
			points.add(point);
		} else {
			Vector3i lastPoint = points.get(points.size() - 1);
			if (!point.equals(lastPoint)) {
				points.add(point);
			}
			distance += lastPoint.distance(point);
		}
	}

	public double getDistance() {
		return distance;
	}

	public int size() {
		return points.size();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("distance", distance);

		JSONArray pathArray = new JSONArray();
		for (Vector3i point : points) {
			pathArray.put(new JSONObject().put("x", point.getX()).put("y", point.getY()).put("z", point.getZ()));
		}
		obj.put("path", pathArray);
		return obj;
	}

}
