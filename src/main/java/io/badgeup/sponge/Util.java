package io.badgeup.sponge;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

public class Util {
	
	public static Optional<String> parseAppIdFromAPIKey(String apiKey) {
		try {
			// Base64 decode the API key
			final byte[] decodedKey = Base64.getDecoder().decode(apiKey);
			JSONObject keyObj = new JSONObject(new String(decodedKey));
			final String appId = keyObj.getString("applicationId");
			return appId.isEmpty() ? Optional.empty() : Optional.of(appId);
		} catch(Exception e) {
			return Optional.empty();
		}
		
	}

	public static JSONObject dataContainerToJSONObject(DataContainer container) {
		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(container);
		StringWriter writer = new StringWriter();
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
	
	public static Optional<Double> safeParseDouble(String raw) {
		try {
			return Optional.of(Double.parseDouble(raw));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<Object> safeGet(JSONObject obj, String key) {
		try {
			return Optional.of(obj.get(key));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public static Optional<JSONObject> safeGetJSONObject(JSONObject obj, String key) {
		try {
			return Optional.of(obj.getJSONObject(key));
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
	
	public static Optional<Double> safeGetDouble(JSONObject obj, String key) {
		try {
			return Optional.of(obj.getDouble(key));
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
