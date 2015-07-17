package com.submarine.game.utils;

public class Constants {
	
	public static final float PPM = 32;
	
	//Play variables
	public static final float WAVE_WIDTH = 0.15f;
	public static final float LIGHT_FADE_TIME = 0.01f;
	
	//Box2d
	public static final String BOX2D_OBJECT = "object";
	public static final String BOX2D_GOAL_USERDATA = "GOAL";
	public static final Object BOX2D_WALL_USERDATA = "WALL";
	
	//categories
	public static final short PLAYER_CATEGORY = 0x001;
	public static final short BULLET_CATEGORY = 0x002;
	public static final short WORLD_CATEGORY  = 0x004;
	
	//masks
	public static final short PLAYER_MASK = WORLD_CATEGORY;
	public static final short BULLET_MASK = WORLD_CATEGORY;
	
	//END OF Box2d
}
