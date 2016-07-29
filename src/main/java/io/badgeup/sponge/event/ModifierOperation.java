package io.badgeup.sponge.event;

public enum ModifierOperation {
	INC, DEC, SET;

	public String getName() {
		return "@" + this.name().toLowerCase();
	}
}