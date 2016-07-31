package io.badgeup.sponge;
import java.io.IOException;
import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;

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
		if(object.has(dataKey)) {
			JSONArray dataArray = object.getJSONArray(dataKey);
			for(int i = 0; i < dataArray.length(); i++) {
				JSONObject data = ((JSONObject) dataArray.get(i)).getJSONObject("ManipulatorData");
				for(String key : data.keySet()) {
					object.put(key, data.get(key));
				}
			}
			object.remove(dataKey);
		}
		
		object.remove("UnsafeData");
		object.remove("ContentVersion");
		
		// For entities
		object.remove("EntityClass"); // EntityType will suffice
	}

}
