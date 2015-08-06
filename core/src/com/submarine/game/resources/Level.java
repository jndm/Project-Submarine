package com.submarine.game.resources;

public class Level {
	
	private String name;
	private Boolean available;
	private String pb;
	private Boolean secretFound;
	
	public Level() {}
	
	public Level(String name, String pb, Boolean available, Boolean secretFound) {
		this.pb = pb;
		this.available = available;
		this.name = name;
		this.secretFound = secretFound;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public String getPb() {
		return pb;
	}

	public void setPb(String pb) {
		this.pb = pb;
	}

	public Boolean isSecretFound() {
		return secretFound;
	}

	public void setSecretFound(Boolean secretFound) {
		this.secretFound = secretFound;
	}
	
	
	
}
