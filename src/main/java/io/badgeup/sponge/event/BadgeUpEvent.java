package io.badgeup.sponge.event;

import io.badgeup.sponge.util.JSONSerializable;
import io.badgeup.sponge.util.ObjectSerializers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

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
     *
     * @param key the identifying string for this type of event
     * @param subject the subject string identifying the user that caused this
     *        event
     * @param modifier the metric impact this event will have
     */
    public BadgeUpEvent(String key, UUID subject, Modifier modifier) {
        this.key = key;
        this.subject = subject;
        this.modifier = modifier;
        this.data = new JSONObject();
        this.options = new JSONObject();
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UUID getSubject() {
        return this.subject;
    }

    /**
     * Add a custom key/value data pair
     *
     * @param key
     * @param value
     */
    @SuppressWarnings("rawtypes")
    public void addDataEntry(final String key, Object value) {
        if (value instanceof Player || value instanceof Cause || value instanceof Class) {
            return;
        }

        if (value instanceof Transform) {
            value = ObjectSerializers.transformToJSONObject(((Transform) value));
        } else if (value instanceof DataSerializable) {
            if (value instanceof Text) {
                value = ((Text) value).toPlain();
            } else {
                value = ObjectSerializers.dataContainerToJSONObject(((DataSerializable) value).toContainer());
            }
        } else if (value instanceof JSONSerializable) {
            value = ((JSONSerializable) value).toJSON();
        } else if (value instanceof List) {
            JSONArray array = new JSONArray();
            for (Object entry : (List) value) {
                if (!(entry instanceof DataSerializable)) {
                    return;
                }
                array.put(ObjectSerializers
                        .dataContainerToJSONObject(((DataSerializable) entry).toContainer()));
            }
            value = array;
        } else {
            Method toStringMethod;
            try {
                toStringMethod = value.getClass().getMethod("toString");
            } catch (NoSuchMethodException e) {
                // Can't be thrown since every class has a toString method
                // through Object
                return;
            }
            if (toStringMethod.getDeclaringClass() == Object.class) {
                // toString has not been implemented and will only produce
                // the class name + the object hash
                // this is useless, so discard it
                return;
            }
        }
        this.data.put(key, value);
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
        return new JSONObject().put("key", this.key).put("subject", this.subject.toString()).put("data", this.data)
                .put("options", this.options)
                .put("modifier", new JSONObject().put(this.modifier.getOperation().getName(), this.modifier.getValue()));
    }

}
