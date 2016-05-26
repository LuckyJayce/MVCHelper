package com.shizhefei.test.models.enties;

public class Movie {
	private String name;
	private double price;
	private String description;
	private String time;

	public Movie(String name, double price, String description, String time) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
