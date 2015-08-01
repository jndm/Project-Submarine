package com.submarine.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.submarine.game.screens.Mainmenu;
import com.submarine.game.screens.Play;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.Constants.Theme;

public class Main extends Game {
	
	public SpriteBatch sb;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	public ExtendViewport gameViewport;
	public ExtendViewport uiViewport;
	
	public AssetManager assetManager;
	
	public Theme theme;

	public void create() {
		
		assetManager = new AssetManager();
		
		sb = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		gameViewport = new ExtendViewport(Constants.VIRTUAL_WIDTH / 20, Constants.VIRTUAL_HEIGHT / 20, cam);
		
		hudCam = new OrthographicCamera();
		uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCam);
		
		theme = Constants.Theme.BLUE;
		
		this.setScreen(new Mainmenu(this));
		
	}
	
	 public void dispose() {
		sb.dispose();
		shapeRenderer.dispose();
        assetManager.dispose();
    }
}