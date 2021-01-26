package net.parkl.ocpp.service;

public class PowerValue {
	public PowerValue(float value, String unit) {
		super();
		this.value = value;
		this.unit = unit;
	}
	private float value;
	private String unit;
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
