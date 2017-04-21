package io.badgeup.sponge.util;

import com.flowpowered.math.vector.Vector3d;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ObjectSerializers {

    public static JSONObject transformToJSONObject(Transform transform) {
        return new JSONObject()
                .put("Position", vector3dToJSONObject(transform.getPosition()))
                .put("Rotation", vector3dToJSONObject(transform.getRotation()))
                .put("Extent", extentToJSONObject(transform.getExtent()));
    }

    public static JSONObject extentToJSONObject(Extent extent) {
        JSONObject data = new JSONObject()
                .put("id", extent.getUniqueId().toString());

        if (extent instanceof World) {
            World w = (World) extent;

            data.put("Difficulty", w.getDifficulty().getId());
            data.put("Dimension", dimensionToJSONObject(w.getDimension()));
        }

        return data;
    }

    public static JSONObject dimensionToJSONObject(Dimension dimension) {
        return new JSONObject()
                .put("Type", dimension.getType().getId());
    }

    public static JSONObject vector3dToJSONObject(Vector3d vector3d) {
        return new JSONObject()
                .put("x", vector3d.getX())
                .put("y", vector3d.getY())
                .put("z", vector3d.getZ());
    }

    public static JSONObject dataContainerToJSONObject(DataContainer container) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            DataFormats.JSON.writeTo(stream, container);
            JSONObject data = new JSONObject(stream.toString());
            cleanData(data);
            return data;
        } catch (IOException e) {
            return new JSONObject();
        }
    }

    private static void cleanData(JSONObject object) {
        if (object.has("Data")) {
            JSONArray dataArray = object.getJSONArray("Data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = ((JSONObject) dataArray.get(i)).getJSONObject("ManipulatorData");
                for (String key : data.keySet()) {
                    object.put(key, data.get(key));
                }
            }
            object.remove("Data");
        }

        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                cleanData((JSONObject) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    Object element = array.get(i);
                    if (element instanceof JSONObject) {
                        cleanData((JSONObject) element);
                    }
                }
            }
        }

        object.remove("UnsafeData");
        object.remove("UnsafeDamage");
        object.remove("ContentVersion");

        // For entities
        object.remove("EntityClass"); // EntityType will suffice

        // For item stack snapshots
        object.remove("TypeClass"); // ItemType will suffice
    }

}
