package com.submarine.game.screens;

import java.util.Iterator;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.resources.Bullet;
import com.submarine.game.resources.GameWorld;
import com.submarine.game.resources.Level;
import com.submarine.game.resources.Player;
import com.submarine.game.ui.Hud;
import com.submarine.game.utils.BulletPool;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.MyContactListener;
import com.submarine.game.utils.MyInputProcessor;
import com.submarine.game.utils.PointLightPool;

public class Play implements Screen {	
	
	//Common stuff
	private Main game;
	private Hud hud;
	private World world;
	private GameWorld gameWorld;
	private Level level;
	private Player player;
	private float timeElapsed = 0;
	private float gameRunningTime = 0;
	
	public enum PlayState {
		PLAY, PAUSED, WIN, LOSE
	}
	
	private PlayState playState;
	
	//Bullet
	private Array<Bullet> activeBullets; 
	private BulletPool bulletPool;
		
	private BitmapFont font = new BitmapFont();
	
	//Lighting
	private RayHandler rayHandler;
	private PointLightPool pointLightPool;
	private Array<PointLight> activeLights;	
	
	public Play(Main game, Level level) {
		this.game = game;
		this.level = level;
	}
	
	@Override
	public void show() {
		playState = PlayState.PLAY;
		
		hud = new Hud(game, this);
		
		world = new World(new Vector2(0, 0f), true);
		world.setContactListener(new MyContactListener(this));
		
		//Lighting
        RayHandler.useDiffuseLight(true);
        RayHandler.setGammaCorrection(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0);
        rayHandler.setCulling(false);                
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(1);
        rayHandler.setShadows(true);
        
        pointLightPool = new PointLightPool(rayHandler, this);
        activeLights = new Array<PointLight>();
		
		//Add level string constant to pass to level when there is more than 1 level (also to Play-class' constructor)
		try {
			gameWorld = new GameWorld(this, game, world, level);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		  
		player = new Player(world, gameWorld.getSpawnpoint(), this, rayHandler); //Create player
		bulletPool = new BulletPool(world);
		activeBullets = new Array<Bullet>();
      
		InputProcessor gameInputProcessor = createInputProcessor();	
		InputProcessor hudInputProcessor = hud.getStage();
		Gdx.input.setInputProcessor(new InputMultiplexer(hudInputProcessor, gameInputProcessor));
	}

	private MyInputProcessor createInputProcessor() {
		return new MyInputProcessor(){
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {	
					case Keys.W:
						player.setUp(true);
					break;	
					case Keys.S:
						player.setDown(true);
					break;	
					case Keys.A:
						player.setLeft(true);	
					break;	
					case Keys.D:
						player.setRight(true);
					break;
					case Keys.ESCAPE:
						game.setScreen(new Mainmenu(game));
					break;
				}
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {	
					case Keys.W:
						player.setUp(false);
					break;
					case Keys.S:
						player.setDown(false);
					break;
					case Keys.A:
						player.setLeft(false);
					break;
					case Keys.D:
						player.setRight(false);
					break;
				}
				return true;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector3 touchpos = new Vector3(screenX, screenY, 0);
				game.cam.unproject(touchpos);
				Bullet bullet = (Bullet) bulletPool.obtain();
				bullet.addToWorld(world);
				bullet.setPosition(player.getBody().getWorldCenter().x, player.getBody().getWorldCenter().y);
				bullet.setVelocity(touchpos.x, touchpos.y);
				bullet.setShootingPoint(player.getBody().getWorldCenter());
				activeBullets.add(bullet);
				return true;
			}
		};
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Draw stuff according to play state
		switch(playState) {
			case PLAY:
				update(delta);
			case PAUSED:
			case WIN:
				game.sb.setProjectionMatrix(game.gameViewport.getCamera().combined);
				game.gameViewport.apply();
				gameWorld.render();

				rayHandler.setCombinedMatrix((OrthographicCamera) game.gameViewport.getCamera());
				rayHandler.updateAndRender();
				
				//Render bullet trail
				game.sb.begin();
				for(Bullet bullet : activeBullets) {
					bullet.render(game.sb, delta);
				}
				game.sb.end();
				
				player.render(game.sb, delta);
				break;
			case LOSE:
				break;
		}
		
		//Always draw hud
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.uiViewport.apply();
		
		hud.render();
		
		game.sb.begin();
			font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), game.uiViewport.getWorldWidth() * 0.8f, game.uiViewport.getWorldHeight() * 0.95f);
		game.sb.end();
		
	}

	private void update(float delta) {
		gameRunningTime += delta;	
		world.step(1/60f, 8, 3);
		player.move();
		updateCamera();
		checkIfBulletsToBeRemoved();
		updateBulletTrail();
		fadeOutLights(delta);
		hud.update(delta);
		checkIfPlayerWon();
	}

	private void checkIfPlayerWon() {
		if(playState == PlayState.WIN) {		
			boolean valueChanged = false;
			if(level.getPb().equals("00:00.00") || hud.getTimeString().compareTo(level.getPb()) < 0) {
				level.setPb(hud.getTimeString());
				valueChanged = true;
			}
			
			if(!level.isPassed()) {
				level.setPassed(true);
				valueChanged = true;
			}
			
			if(valueChanged) {
				game.saveManager.saveDataValue(level.getName(), level);
			}
			
			hud.showEndingStatusDialog(true);
		}
	}

	private void updateBulletTrail() {
		for(Bullet b : activeBullets) {
			b.updateBulletTrail();
		}
	}

	private void fadeOutLights(float delta) {
		if(activeLights.size != 0) {
			timeElapsed += delta;
			if(Constants.LIGHT_FADE_TIME <= timeElapsed) {
				Iterator<PointLight> i = activeLights.iterator();
				while(i.hasNext()) {
					PointLight pl = i.next();
					float newAlpha = pl.getColor().a - 0.01f;
					System.out.println(pl.getColor().a);
					pl.setColor(pl.getColor().r, pl.getColor().g, pl.getColor().b, newAlpha);
					if(newAlpha <= 0) {
						activeLights.removeValue(pl, false);
						pointLightPool.free(pl);
					}
				}
				timeElapsed -= Constants.LIGHT_FADE_TIME;
			}
		} else {
			timeElapsed = 0;
		}		
	}

	private void checkIfBulletsToBeRemoved() {	
		Iterator<Bullet> i = activeBullets.iterator();
		while(i.hasNext()) {
			Bullet bullet = i.next();
			
			if(!bullet.isBodyRemoved() && bullet.shouldBeRemoved()) {
				world.destroyBody(bullet.getBody());
				bullet.setBodyRemoved(true);
				bullet.allowParticleCompletion();
			}
			
			if(bullet.isParticleEffectComplete() && bullet.shouldBeRemoved()) {
				activeBullets.removeValue(bullet, true);
				bulletPool.free(bullet);
			}
			
		}
	}

	private void updateCamera() {
		Vector2 playerpos = player.getBody().getPosition();
		game.cam.position.set(playerpos.x, playerpos.y, 0);
		game.cam.update();
	}

	@Override
	public void resize(int width, int height) {
		game.gameViewport.update(width, height, true);
		game.uiViewport.update(width, height, true);
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
		//dispose();
	}

	@Override
	public void dispose() {
		hud.dispose();
		
		world.dispose();
		gameWorld.dispose();
		player.dispose();
		rayHandler.dispose();
		
		for(Bullet b : activeBullets) {
			b.dispose();
		}
		
		bulletPool.freeAll(activeBullets);
		bulletPool.clear();
		activeBullets.clear();
		
		font.dispose();
		
		pointLightPool.freeAll(activeLights);
		pointLightPool.clear();
		activeLights.clear();
	}

	public void addPointLight(Vector2 collisionPoint) {
		PointLight pl = pointLightPool.obtain();
		pl.setPosition(collisionPoint);
		pl.setColor(Constants.BLUE);
		activeLights.add(pl);
	}

	public float getGameRunningTime() {
		return gameRunningTime;
	}

	public Level getLevel() {
		return level;
	}

	public PlayState getPlayState() {
		return playState;
	}

	public void setPlayState(PlayState playState) {
		this.playState = playState;
	}
	
}
