package io.badgeup.sponge.event;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ModifierSerializer extends JsonSerializer<Modifier> {

	@Override
	public void serialize(Modifier value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeFieldName(value.getOperation().getName());
		jgen.writeObject(value.getValue());
		jgen.writeEndObject();
	}
}
