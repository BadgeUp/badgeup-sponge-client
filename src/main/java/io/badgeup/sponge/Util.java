package io.badgeup.sponge;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

public class Util {

	public static JSONObject dataContainerToJSONObject(DataContainer container) {
		StringWriter writer = new StringWriter();
		ConfigurationNode node = SimpleConfigurationNode.root();
		ConfigurateTranslator.instance().translateContainerToData(node, container);
		try {
			JSONConfigurationLoader.builder().build().saveInternal(node, writer);
			return new JSONObject(writer.toString());
		} catch (IOException e) {
			return new JSONObject();
		}
	}

	public static void cleanData(JSONObject object) {
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
	
	public static Optional<Object> safeGet(JSONObject obj, String key) {
		try {
			return Optional.of(obj.get(key));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<BigDecimal> safeGetBigDecimal(JSONObject obj, String key) {
		try {
			return Optional.of(obj.getBigDecimal(key));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<Integer> safeGetInt(JSONObject obj, String key) {
		try {
			return Optional.of(obj.getInt(key));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<String> safeGetString(JSONObject obj, String key) {
		try {
			return Optional.of(obj.getString(key));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<List<Object>> safeGetList(JSONObject obj, String key) {
		try {
			List<Object> entries = new ArrayList<>();
			obj.getJSONArray(key).forEach(entries::add);
			return Optional.of(entries);
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<Text> deserializeText(Object serialized) {
		if(serialized == null) {
			return Optional.empty();
		} else if(serialized instanceof String) {
			return Optional.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked((String) serialized));
		} else if(serialized instanceof JSONObject) {
			return Optional.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked(serialized.toString()));
		} else {
			return Optional.empty();
		}
	}

}