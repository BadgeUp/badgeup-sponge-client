package io.badgeup.sponge.event;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModifierOperation {
	INC, DEC, SET;

	@JsonValue
	public String getName() {
		return "@" + this.name().toLowerCase();
	}
}