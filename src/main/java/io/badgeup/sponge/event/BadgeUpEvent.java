package io.badgeup.sponge.event;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONObject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

public class BadgeUpEvent {

	private String key;

	private UUID subject;

	// user provided data
	private JSONObject data;
	
	// BadgeUp event options
	private JSONObject options;

	private Modifier modifier;

	/**
	 * Constructor for a BadgeUp event
	 * @param key the identifying string for this type of event
	 * @param subject the subject string identifying the user that caused this event
	 * @param modifier the metric impact this event will have 
	 */
	public BadgeUpEvent(String key, UUID subject, Modifier modifier) {
		this.key = key;
		this.subject = subject;
		this.modifier = modifier;
		this.data = new JSONObject();
		this.options = new JSONObject();
	}

	/**
	 * Add a custom key/value data pair
	 * @param key
	 * @param ds
	 */
	public void addDataEntry(final String key, Object ds) {
		if(ds instanceof DataContainer) {
			ds = dataContainerToJSONObject((DataContainer) ds);
		}
		data.put(key, ds);
	}

	/**
	 * Marks the event to be discarded (not stored) after it is processed
	 */
	public void setDiscardable(boolean discardable) {
		if (discardable) {
			this.options.put("discard", discardable);
		} else {
			this.options.remove("discard");
		}
	}
	
	public JSONObject build() {
		return new JSONObject()
				.put("key", key)
				.put("subject", subject.toString())
				.put("data", data)
				.put("options", options)
				.put("modifier", new JSONObject().put(modifier.getOperation().getName(), modifier.getValue()));
	}
	
	private JSONObject dataContainerToJSONObject(DataContainer container) {
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
	
}
