package com.submarine.game.utils;

public class Constants {
	
	//Play variables
	public static final float WAVE_WIDTH = 0.15f;
	public static final float LIGHT_FADE_TIME = 0.01f;
	
	//Box2d categories
	public static final short PLAYER_CATEGORY = 0x001;
	public static final short BULLET_CATEGORY = 0x002;
	public static final short WORLD_CATEGORY  = 0x004;
	
	//Box2d masks
	public static final short PLAYER_MASK = WORLD_CATEGORY;
	public static final short BULLET_MASK = WORLD_CATEGORY;
}
