package io.badgeup.sponge.event;

import java.util.UUID;

import org.json.JSONObject;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import io.badgeup.sponge.Util;

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
	
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Add a custom key/value data pair
	 * @param key
	 * @param value
	 */
	public void addDataEntry(final String key, Object value) {
		if(value instanceof DataSerializable) {
			if(value instanceof Player) {
				return;
			} else if(value instanceof Text) {
				value = ((Text) value).toPlain();
			} else {
				JSONObject serializedObject = Util.dataContainerToJSONObject(((DataSerializable) value).toContainer());
				Util.cleanData(serializedObject);
				value = serializedObject;
			}
		} else {
			if(value instanceof Enum) {
				value = value.toString();
				System.out.println(value);
			} else {
				return;
			}
		}
		data.put(key, value);
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
	
}
