package com.submarine.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.submarine.game.screens.Mainmenu;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.Constants.Theme;

public class Main extends Game {
	
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;
	public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH / (float)VIRTUAL_HEIGHT;
	public static final float PPM = 32;
	
	public static final float STEP = 1 / 60f;
	
	public SpriteBatch sb;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	private Viewport viewport, viewport2;
	
	public AssetManager assetManager;
	
	public Theme theme;

	public void create() {
		
		assetManager = new AssetManager();
		
		sb = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, VIRTUAL_WIDTH / Constants.PPM, VIRTUAL_HEIGHT / Constants.PPM);
		viewport = new FillViewport(VIRTUAL_WIDTH * ASPECT_RATIO, VIRTUAL_HEIGHT, cam);
		
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, VIRTUAL_WIDTH / Constants.PPM, VIRTUAL_HEIGHT / Constants.PPM);
		viewport2 = new FillViewport(VIRTUAL_WIDTH * ASPECT_RATIO, VIRTUAL_HEIGHT, hudCam);
		
		theme = Constants.Theme.BLUE;
		
		this.setScreen(new Mainmenu(this));
		
	}
	
	 public void dispose() {
		sb.dispose();
		shapeRenderer.dispose();
        assetManager.dispose();
    }
}