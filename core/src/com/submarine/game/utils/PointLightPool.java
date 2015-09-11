package com.submarine.game.utils;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;
import com.submarine.game.screens.Play;

public class PointLightPool extends Pool<PointLight>{

	private RayHandler rayHandler;
	private Play play;
	
	public PointLightPool(RayHandler rayHandler, Play play) {
		super(20);
		this.rayHandler = rayHandler;
		this.play = play;
	}
	
	@Override
	protected PointLight newObject() {
		PointLight pointlight = new PointLight(rayHandler, 16);
		pointlight.setDistance(3);
		return pointlight;
	}
}
