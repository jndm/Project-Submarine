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
import com.submarine.game.screens.Mainmenu.OptionsClickListener;

public class LevelSelection implements Screen {
	
	private Main game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	private String currentTheme;
	
	public LevelSelection(Main game, String currentTheme) {
		this.game = game;
		this.currentTheme = currentTheme;
	}

	@Override
	public void show() {
		atlas = game.assetManager.get(currentTheme);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("ui/ui_skin.json"), atlas);
		
		mastertable = new Table(skin);
		mastertable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		TextButton playButton = new TextButton("Play", skin, "button");
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new Play(game));
			}
		});
		
		stage.addActor(mastertable);

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

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		atlas.dispose();
		game.assetManager.unload(currentTheme);
	}
}
