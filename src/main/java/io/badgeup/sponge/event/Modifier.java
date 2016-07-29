package io.badgeup.sponge.event;

public class Modifier {

	private ModifierOperation operation;
	private double value;

	public Modifier(ModifierOperation operation, double value) {
		this.operation = operation;
		this.value = value;
	}

	public ModifierOperation getOperation() {
		return operation;
	}

	public void setOperation(ModifierOperation operation) {
		this.operation = operation;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}
