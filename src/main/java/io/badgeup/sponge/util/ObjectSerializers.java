package io.badgeup.sponge.util;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.io.IOException;
import java.io.StringWriter;

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
        ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(container);
        StringWriter writer = new StringWriter();
        try {
            JSONConfigurationLoader.builder().build().saveInternal(node, writer);
            return cleanData(new JSONObject(writer.toString()));
        } catch (IOException e) {
            return new JSONObject();
        }
    }

    private static JSONObject cleanData(JSONObject object) {
        final String dataKey = "Data";
        if (object.has(dataKey)) {
            JSONArray dataArray = object.getJSONArray(dataKey);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = ((JSONObject) dataArray.get(i)).getJSONObject("ManipulatorData");
                for (String key : data.keySet()) {
                    object.put(key, data.get(key));
                }
            }
            object.remove(dataKey);
        }

        removeFields(object);
        return object;
    }

    private static void removeFields(JSONObject object) {
        object.remove("UnsafeData");
        object.remove("ContentVersion");

        // For entities
        object.remove("EntityClass"); // EntityType will suffice

        // For item stack snapshots
        object.remove("TypeClass"); // ItemType will suffice

        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                removeFields((JSONObject) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    Object element = array.get(i);
                    if (element instanceof JSONObject) {
                        removeFields((JSONObject) element);
                    }
                }
            }
        }
    }

}
