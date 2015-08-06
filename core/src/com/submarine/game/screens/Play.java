package com.submarine.game.screens;

import java.util.Iterator;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.resources.Bullet;
import com.submarine.game.resources.GameWorld;
import com.submarine.game.resources.Level;
import com.submarine.game.resources.Player;
import com.submarine.game.utils.BulletPool;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.MyContactListener;
import com.submarine.game.utils.MyInputProcessor;
import com.submarine.game.utils.PointLightPool;
import com.submarine.game.utils.Utils;

public class Play implements Screen {
	
	//Hud
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Label timerLabel;
	
	//Common stuff
	private Main game;
	private World world;
	private GameWorld gameWorld;
	private Level level;
	private Player player;
	private float timeElapsed = 0;
	private float gameRunningTime = 0;
	private Color currentThemeColor;
	
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
		checkThemeColor();
		
		// HUD
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
		
		timerLabel = new Label(gameRunningTime+"", skin, "label");
		mastertable.add(timerLabel);
		mastertable.row();
		mastertable.debug();
		
		stage.addActor(mastertable);
		// END OF HUD 
		
		// CREATE OTHER
		world = new World(new Vector2(0, 0f), true);
		world.setContactListener(new MyContactListener(this));
		
		//Add level string constant to pass to level when there is more than 1 level (also to Play-class' constructor)
		try {
			gameWorld = new GameWorld(this, game, world, level);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		player = new Player(world, gameWorld.getSpawnpoint(), this); //Create player
		bulletPool = new BulletPool(world, currentThemeColor);
		new Array<Bullet>();
		activeBullets = new Array<Bullet>();

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
        
		createInputProcessor();	
	}

	private void createInputProcessor() {
		Gdx.input.setInputProcessor(new MyInputProcessor(){
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
		});
	}

	@Override
	public void render (float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
		
		//Gdx.app.log("pool stats", "active: " + beamParticles.size + " | free: " + beamParticlePool.getFree() + "/" + beamParticlePool.max + " | record: " + beamParticlePool.peak);
	
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.uiViewport.apply();
		game.sb.begin();
			font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), game.uiViewport.getWorldWidth() * 0.90f, game.uiViewport.getWorldHeight() * 0.95f);
			Gdx.app.log("", game.uiViewport.getWorldWidth()+" "+game.uiViewport.getWorldHeight());
		game.sb.end();
		
		stage.act();
		stage.draw();
		
	}

	private void update(float delta) {
		gameRunningTime += delta;
		
		world.step(1/60f, 8, 3);
		player.move();
		updateCamera();
		checkIfBulletsToBeRemoved();
		updateBulletTrail();
		fadeOutLights(delta);
		updateTimer();
	}

	private void updateTimer() {
		timerLabel.setText(gameRunningTime+"");
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
		dispose();
	}

	@Override
	public void dispose() {
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
		pl.setColor(currentThemeColor);
		activeLights.add(pl);
	}

	public float getGameRunningTime() {
		return gameRunningTime;
	}
	
	private void checkThemeColor() {
		if(game.theme == Constants.Theme.RED) {
			currentThemeColor = Constants.RED;
		} else if(game.theme == Constants.Theme.GREEN) {
			currentThemeColor = Constants.GREEN;
		} else if(game.theme == Constants.Theme.BLUE) {
			currentThemeColor = Constants.BLUE;
		}
	}

	public Color getCurrentThemeColor() {
		return currentThemeColor;
	}
	
	/*STUFF IN CASE EVER NEEDED
		//OLD BULLET TRAIL RENDERING WITH SHAPERENDERER:
		game.shapeRenderer.setProjectionMatrix(game.cam.combined);
		game.shapeRenderer.setColor(0.41f, 0.78f, 1f, 1f);
		game.shapeRenderer.begin(ShapeType.Filled);
		for(Bullet b : activeBullets){
			Vector2 bulletpos = b.getBody().getPosition();
			if(b.getCollisionPoints().size == 0) {	// no collision points
				game.shapeRenderer.rectLine(b.getShootingPoint(), bulletpos, Constants.WAVE_WIDTH);
			} else {
				game.shapeRenderer.rectLine(b.getShootingPoint(), b.getCollisionPoints().first(), Constants.WAVE_WIDTH); // 1 or more collision points
				if(b.getCollisionPoints().size > 1) {	//More than 1 collision points
					for(int i=0; i<b.getCollisionPoints().size-1; i++) {
						game.shapeRenderer.rectLine(b.getCollisionPoints().get(i), b.getCollisionPoints().get(i+1), Constants.WAVE_WIDTH);
					}
					game.shapeRenderer.rectLine(b.getCollisionPoints().get(b.getCollisionPoints().size - 1), bulletpos, Constants.WAVE_WIDTH);	// always draw last line to bullet position
				} else {
					game.shapeRenderer.rectLine(b.getCollisionPoints().first(), bulletpos, Constants.WAVE_WIDTH); // always draw last line to bullet position
				}
			}
		}
		game.shapeRenderer.end();
		//ENDOF

		//KEEPS CAMERA INSIDE THE GAMEWORLD:
		float camx = game.cam.position.x;
		float camy = game.cam.position.y;
		float camw = game.cam.viewportWidth;
		float camh = game.cam.viewportHeight;
		
		//Horizontal
		if(camx - camw/2 <= 0 && camx + camw/2 >= LEVEL_WIDTH) {
			game.cam.position.x = playerpos.x;
		}
		
		//Vertical
		if(camy - camh/2 <= 0 && camy + camh/2 >= LEVEL_HEIGHT) {
			game.cam.position.y = playerpos.y;
		}
		//ENDOF
	*/
}
