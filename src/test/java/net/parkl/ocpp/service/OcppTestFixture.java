package net.parkl.ocpp.service;

import org.springframework.stereotype.Component;

@Component
public class OcppTestFixture {
	private Integer stopValue;
	private Integer startValue;
	
	public int getStopValue() {
		if (stopValue==null) {
			throw new IllegalStateException("No stop value registered");
		}
		return stopValue;
	}
	
	public void registerStopValue(int val) {
		this.stopValue=val;
	}

	public int getStartValue() {
		if (startValue==null) {
			throw new IllegalStateException("No start value registered");
		}
		return startValue;
	}
	
	public void registerStartValue(int val) {
		this.startValue=val;
	}
}
