package com.submarine.game.utils;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.utils.Pool;

public class PointLightPool extends Pool<PointLight>{

	private RayHandler rayHandler;
	
	public PointLightPool(RayHandler rayHandler) {
		super(20);
		this.rayHandler = rayHandler;
	}
	
	@Override
	protected PointLight newObject() {
		PointLight pointlight = new PointLight(rayHandler, 16);
		pointlight.setDistance(3);
		return pointlight;
	}
}
