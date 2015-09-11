package com.submarine.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.submarine.game.resources.Level;
import com.submarine.game.screens.Mainmenu;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.SaveManager;

public class Main extends Game {
	
	public SpriteBatch sb;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	public ExtendViewport gameViewport;
	public ExtendViewport uiViewport;
	public SaveManager saveManager;
	public Options options;
	
	public AssetManager assetManager;

	public void create() {
		
		assetManager = new AssetManager();
		
		sb = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		gameViewport = new ExtendViewport(Constants.VIRTUAL_WIDTH / 20, Constants.VIRTUAL_HEIGHT / 20, cam);
		
		hudCam = new OrthographicCamera();
		uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCam);
		
		saveManager = new SaveManager();
		
		if(saveManager.getAllData().size == 0) {	 // if first time launching the game
			generateLevelData();
		}
		
		options = new Options();
		options.setSoundOn(true);
		
		this.setScreen(new Mainmenu(this));
		
	}

	private void generateLevelData() {
		for(int i=0; i < Constants.MAXLEVELS; i++) {
			Level level = null;
			if(i < 5) {
				level = new Level("level"+(i+1), "00:00.00", true, false);	//set first level available
			} else {
				level = new Level("level"+(i+1), "00:00.00", false, false);
			}
			saveManager.saveDataValue("level"+(i+1), level);
		}
	}

	public void dispose() {
		sb.dispose();
		shapeRenderer.dispose();
        assetManager.dispose();
    }
}