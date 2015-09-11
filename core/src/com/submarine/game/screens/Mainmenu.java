package com.submarine.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.submarine.game.Main;
import com.submarine.game.ui.OptionsDialog;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.Utils;

public class Mainmenu implements Screen {
	
	private Main game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	private String currentTheme;
	
	// Actor sizes
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;
	
	//Font sizes
	private float MAINBUTTON_FONT_SIZE;
	private float TITLE_FONT_SIZE;
	
	private void createConstants() { // Since stage is 
		BUTTON_WIDTH = stage.getWidth() / 2f;
		BUTTON_HEIGHT = stage.getHeight() / 6f;	
		
		MAINBUTTON_FONT_SIZE = BUTTON_WIDTH / 8f;             
		TITLE_FONT_SIZE = stage.getWidth() * 0.1f;
	}
	
	public Mainmenu(Main game) {
		this.game = game;
	} 
	
	@Override
	public void show() {
		applySettings(false);
	}

	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.sb.setProjectionMatrix(stage.getCamera().combined);
		stage.getViewport().apply();
		
		stage.act();
		stage.draw();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		game.assetManager.unload(Constants.BLUE_UI_ATLAS);
		skin.dispose();
		stage.dispose();
		atlas.dispose();	
	}

	public void applySettings(boolean themeChanged) {		
		game.assetManager.load(Constants.BLUE_UI_ATLAS, TextureAtlas.class);
		game.assetManager.finishLoading();
		
		atlas = game.assetManager.get(Constants.BLUE_UI_ATLAS);
		
		stage = new Stage(game.uiViewport, game.sb);
		
		createConstants();
		
		skin = new Skin();
		skin.add("font", Utils.createFont(Constants.FONT_KENFACTOR_PATH, 20));
		skin.add("mainbuttonfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, MAINBUTTON_FONT_SIZE));
		skin.add("titlefont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, TITLE_FONT_SIZE));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal(Constants.MAIN_MENU_SKIN_PATH));
		
		mastertable = new Table(skin);
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		
		mastertable.add(new Label("Submarine", skin, "title")).padBottom(BUTTON_HEIGHT * 0.55f);
		mastertable.row();
		
		addMainButtonsToMasterTable();
	
		stage.addActor(mastertable);
		Gdx.input.setInputProcessor(stage);		
	}
	
	private void addMainButtonsToMasterTable() {
		TextButton playButton = new TextButton("Play", skin, "mainbutton");
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.run(new Runnable() {
				    public void run () {
				    	game.setScreen(new LevelSelection(game, currentTheme));
				    }
				})));
			}
		});
		
		TextButton optionsButton = new TextButton("Options", skin, "mainbutton");
		optionsButton.addListener(new OptionsClickListener(this));
		
		TextButton exitButton = new TextButton("Exit", skin, "mainbutton");
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		mastertable.add(playButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
		mastertable.row();
		mastertable.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
		mastertable.row();
		mastertable.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
	}

	
	public class OptionsClickListener extends ClickListener { //Options clicklistener is here to prevent too many different classes
		
		private Mainmenu menu;
		
		public OptionsClickListener(Mainmenu menu) {
			this.menu = menu;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			new OptionsDialog("Options", skin, game, menu).show(stage);
		}
		
	}	
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
