package io.badgeup.sponge.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BadgeUpEvent {

	private String key;

	private UUID subject;

	// user provided data
	private HashMap<String, Object> data;
	
	// BadgeUp event options
	private HashMap<String, Object> options;

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
		this.data = new HashMap<>();
		this.options = new HashMap<>();
	}

	/**
	 * Constructor for a BadgeUp event
	 * @param key the identifying string for this type of event
	 * @param subject the subject string identifying the user that caused this event
	 * @param modifier the metric impact this event will have
	 * @param data Extra data to include with this event
	 */
	public BadgeUpEvent(String key, UUID subject, Modifier modifier, HashMap<String, Object> data, HashMap<String, Object> options) {
		this(key, subject, modifier);
		this.data = data;
		this.options = options;
	}
	
	/**
	 * Add a custom key/value data pair
	 * @param key
	 * @param ds
	 */
	public void addDataEntry(String key, Object ds) {
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
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public UUID getSubject() {
		return subject;
	}

	public void setSubject(UUID ownerId) {
		this.subject = ownerId;
	}

	public HashMap<String, Object> getData() {
		return data;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}
	
	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(HashMap<String, Object> options) {
		this.options = options;
	}

	public Modifier getModifier() {
		return modifier;
	}

	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
	}

}
