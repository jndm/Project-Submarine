package com.submarine.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.submarine.game.screens.Play;

public class Main extends Game {
	
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;
	public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH / (float)VIRTUAL_HEIGHT;
	public static final float PPM = 32;
	
	public static final float STEP = 1 / 60f;
	
	public SpriteBatch sb;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	
	private AssetManager assetManager;
	private Viewport viewport, viewport2;
	
	public void create() {
		
		assetManager = new AssetManager();
		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, VIRTUAL_WIDTH / PPM, VIRTUAL_HEIGHT / PPM);
		viewport = new FillViewport(VIRTUAL_WIDTH * ASPECT_RATIO, VIRTUAL_HEIGHT, cam);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, VIRTUAL_WIDTH / PPM, VIRTUAL_HEIGHT / PPM);
		viewport2 = new FillViewport(VIRTUAL_WIDTH * ASPECT_RATIO, VIRTUAL_HEIGHT, hudCam);
		this.setScreen(new Play(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	 public void dispose() {
		sb.dispose();
        assetManager.dispose();
    }
}
