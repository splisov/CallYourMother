package com.callyourmother.data;

public class Circle {
	private long circleId = -1;
	private String description;

	public Circle(String description) {
		this.description = description;
	}

	public Circle(long circleId, String description) {
		this.circleId = circleId;
		this.description = description;
	}
	
	public long getCircleId() {
		return circleId;
	}

	public void setCircleId(long circleId) {
		this.circleId = circleId;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
