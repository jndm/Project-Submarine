package com.submarine.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.submarine.game.Main;
import com.submarine.game.ui.OptionsDialog;
import com.submarine.game.utils.Constants;

public class Mainmenu implements Screen {
	
	private Main game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	private String currentTheme;
	
	public Mainmenu(Main game) {
		this.game = game;
	}

	@Override
	public void show() {
		applySettings(false);
		Gdx.input.setInputProcessor(stage);
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
		dispose();
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		atlas.dispose();	
	}

	public void applySettings(boolean themeChanged) {
		
		//Dispose last loaded items if they have been changed
		if(themeChanged && game.assetManager.isLoaded(currentTheme)) {
			game.assetManager.unload(currentTheme);
			dispose();
		}
		
		if(game.theme == Constants.Theme.RED) {
			currentTheme = Constants.RED_UI_ATLAS;
		} else if(game.theme == Constants.Theme.GREEN) {
			currentTheme = Constants.GREEN_UI_ATLAS;
		} else if(game.theme == Constants.Theme.BLUE) {
			currentTheme = Constants.BLUE_UI_ATLAS;
		}
		
		game.assetManager.load(currentTheme, TextureAtlas.class);
		game.assetManager.finishLoading();
		
		atlas = game.assetManager.get(currentTheme);
		
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("ui/ui_skin.json"), atlas);
		
		mastertable = new Table(skin);
		mastertable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		TextButton playButton = new TextButton("Play", skin, "button");
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new LevelSelection(game, currentTheme));
			}
		});
		
		TextButton optionsButton = new TextButton("Options", skin, "button");
		optionsButton.addListener(new OptionsClickListener(this));
		
		TextButton exitButton = new TextButton("Exit", skin, "button");
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		mastertable.add(playButton);
		mastertable.row();
		mastertable.add(optionsButton);
		mastertable.row();
		mastertable.add(exitButton);
		
		stage.addActor(mastertable);
		
	}

	//Options clicklistener is here to prevent too many different classes
	public class OptionsClickListener extends ClickListener {
		
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
