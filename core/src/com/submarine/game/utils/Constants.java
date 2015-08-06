package com.submarine.game.utils;

import com.badlogic.gdx.graphics.Color;

public class Constants {
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;
	public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH / (float)VIRTUAL_HEIGHT;
	public static final float STEP = 1 / 60f;
	public static final float PPM = 32;
	public static enum Theme {
		RED, BLUE, GREEN
	}
	public static final Color BLUE 	= new Color( 0.41f, 0.78f, 1f, 1f);
	public static final Color RED	= new Color( 1f, 0.19f, 0f, 1f);
	public static final Color GREEN = new Color( 0.49f, 0.78f, 0.31f, 1f);
	public static final Color WHITE = new Color( 1f, 1f, 1f, 1f);
	
	//Level data
	public static final int MAXLEVELS = 60;
	
	//Skin paths
	public static final String LEVEL_SELECT_SKIN_PATH = "ui/levelselect_skin.json";
	public static final String MAIN_MENU_SKIN_PATH = "ui/mainmenu_skin.json";
	public static final String HUD_SKIN_PATH = "ui/hud_skin.json";
	
	//Font paths
	public static final String FONT_KENFACTOR_PATH = "fonts/kenvector_future.ttf";
	
	//Atlasses
	public static final String BLUE_UI_ATLAS  = "ui/bluetheme/bluetheme_uipack.pack";
	public static final String RED_UI_ATLAS   = "ui/redtheme/redtheme_uipack.pack";
	public static final String GREEN_UI_ATLAS = "ui/greentheme/greentheme_uipack.pack";
	
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
