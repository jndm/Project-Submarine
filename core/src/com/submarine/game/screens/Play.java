package com.submarine.game.screens;

import java.util.Iterator;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.resources.Bullet;
import com.submarine.game.resources.Level;
import com.submarine.game.resources.Player;
import com.submarine.game.utils.BulletPool;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.MyContactListener;
import com.submarine.game.utils.MyInputProcessor;
import com.submarine.game.utils.PointLightPool;

public class Play implements Screen {
	
	//Common stuff
	private Main game;
	private World world;
	private Level level;
	private Player player;
	private float timeElapsed = 0;
	private float gameRunningTime = 0;
	private Color currentThemeColor;
	
	//Bullet
	private Array<Bullet> activeBullets; 
	private Array<Bullet> bulletsToBeRemoved;
	private BulletPool bulletPool;
	
	//Testing particle-effect bullet trail
	private ParticleEffect beam;
	private ParticleEffectPool beamParticlePool;
	private Array<PooledEffect> beamParticles;
		
	private BitmapFont font = new BitmapFont();
	
	//Lighting
	private RayHandler rayHandler;
	private PointLightPool pointLightPool;
	private Array<PointLight> activeLights;	
	
	public Play(Main game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		
		checkThemeColor();
		
		world = new World(new Vector2(0, 0f), true);
		world.setContactListener(new MyContactListener(this));
		
		//Add level string constant to pass to level when there is more than 1 level (also to Play-class' constructor)
		try {
			level = new Level(this, game, world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		player = new Player(world, level.getSpawnpoint(), this); //Create player
		bulletPool = new BulletPool(world);
		bulletsToBeRemoved = new Array<Bullet>();
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
        
        //Beam particle-effect
        beam = new ParticleEffect();
        beam.load(Gdx.files.internal("effects/beam.p"), Gdx.files.internal("effects"));
        beam.start();
        
        beamParticlePool = new ParticleEffectPool(beam, 0, 200); //With 2sec timer max active particles are 120
        beamParticles = new Array<PooledEffect>();
        
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
						dispose();
						game.setScreen(new Menu(game));
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
		game.sb.setProjectionMatrix(game.cam.combined);

		level.render();
		
		rayHandler.setCombinedMatrix(game.cam);
		rayHandler.updateAndRender();
		
		player.render(game.sb, delta);
		
		//Render bullet trail
		
		game.sb.begin();
		for(PooledEffect effect : beamParticles) {
			effect.draw(game.sb, delta);
			if(effect.isComplete()) {
				beamParticles.removeValue(effect, true);
				effect.free();
			}
		}
		game.sb.end();
		
		//Gdx.app.log("pool stats", "active: " + beamParticles.size + " | free: " + beamParticlePool.getFree() + "/" + beamParticlePool.max + " | record: " + beamParticlePool.peak);
	
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.sb.begin();
			font.getData().setScale(1/64f);
			font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), Main.VIRTUAL_WIDTH * 0.5f, Main.VIRTUAL_HEIGHT * 0.5f);
		game.sb.end();
		
	}

	private void update(float delta) {
		world.step(1/60f, 8, 3);
		player.move();
		updateCamera();
		checkIfBulletsToBeRemoved();
		addBulletTrail();
		fadeOutLights(delta);
		
		gameRunningTime += delta;
	}

	private void addBulletTrail() {
		for(Bullet b : activeBullets) {
			Vector2 bulletpos = b.getBody().getPosition();
			PooledEffect effect = beamParticlePool.obtain();
			effect.setPosition(bulletpos.x, bulletpos.y);
			for(ParticleEmitter emitter :  effect.getEmitters()) {
				float[] color = { currentThemeColor.r, currentThemeColor.g, currentThemeColor.b };
				emitter.getTint().setColors(color);
				emitter.getRotation().setLow(b.getAngle());
				emitter.getRotation().setHigh(b.getAngle());
			}
			beamParticles.add(effect);
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
		Iterator<Bullet> i = bulletsToBeRemoved.iterator();
		while(i.hasNext()) {
			Bullet bullet = i.next();
			world.destroyBody(bullet.getBody());
			activeBullets.removeValue(bullet, false);
			bulletPool.free(bullet);
		}
		bulletsToBeRemoved.clear();
		bulletsToBeRemoved.shrink();
	}

	private void updateCamera() {
		Vector2 playerpos = player.getBody().getPosition();
		game.cam.position.set(playerpos.x, playerpos.y, 0);
		game.cam.update();
	}

	@Override
	public void resize(int width, int height) {
		game.cam.setToOrtho(false, width / Constants.PPM , height / Constants.PPM);
		game.cam.update();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		world.dispose();
		level.dispose();
		player.dispose();
		rayHandler.dispose();
	}

	public void addBulletToBeRemoved(Bullet bullet) {
		bulletsToBeRemoved.add(bullet);
		//System.out.println("Added bullet");
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
