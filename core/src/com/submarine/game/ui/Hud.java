package com.submarine.game.ui;

import java.text.DecimalFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.submarine.game.Main;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.Utils;

public class Hud {
	
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Label timerLabel;
	
	//Timer
	private float gameTime = 0;
	private String timeString = "00:00:00";
	
	public Hud(Main game) {
		game.assetManager.load(Constants.BLUE_UI_ATLAS, TextureAtlas.class);
		game.assetManager.finishLoading();
		
		atlas = game.assetManager.get(Constants.BLUE_UI_ATLAS);
		
		stage = new Stage(game.uiViewport, game.sb);
		
		skin = new Skin();
		skin.add("font", Utils.createFont(Constants.FONT_KENFACTOR_PATH, 20));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal(Constants.HUD_SKIN_PATH));
		
		Table mastertable = new Table();
		mastertable = new Table(skin);
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		
		timerLabel = new Label("00:00:00", skin, "label");
		mastertable.add(timerLabel).left().padLeft(stage.getWidth() * 0.01f);
		mastertable.row();
		mastertable.add().expand().fill();
		mastertable.debug();
		
		stage.addActor(mastertable);
	}
	
	public void render() {
		stage.act(); 
		stage.draw();
	}
	
	public void update(float delta) {
		updateTimer(delta);
		
	}

	private void updateTimer(float delta) {
		gameTime += delta;
	
		int minutes = (int) (gameTime / 60);
		int seconds = (int) (gameTime % 60);
		int milliseconds = (int) ((gameTime % 60 - seconds) * 1000);
		
		String mstext = String.format("%03d", milliseconds);
		String stext = String.format("%02d", seconds);
		String mintext = String.format("%02d", minutes);
		
		timeString = mintext+":"+stext+"."+mstext;
		
		timerLabel.setText(timeString);
	}

	public String getTimeString() {
		return timeString;
	}

}
