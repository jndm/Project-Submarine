package com.submarine.game.screens;

import java.util.Iterator;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.utils.BulletPool;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.MyContactListener;
import com.submarine.game.utils.MyInputProcessor;
import com.submarine.resourses.Bullet;
import com.submarine.resourses.Player;

public class Play implements Screen {

	private Main game;
	private World world;
	private Box2DDebugRenderer box2dRenderer;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Player player;
	
	private Array<Bullet> activeBullets; 
	private Array<Bullet> bulletsToBeRemoved;
	private BulletPool bulletPool;
	
	private Sprite bulletTrail;
	private ShapeRenderer shapeRenderer;
	private Vector2 shootPos;
		
	private BitmapFont font = new BitmapFont();
	
	//TESTING remove after
	private Texture testBg;
	private RayHandler rayHandler;
	private PointLight pointlight;
	//EOF Testing
	
	//Shaders
	private ShaderProgram defaultShader;
	private ShaderProgram finalShader;
	
	public static final float ambientIntensity = .7f;
	public static final Vector3 ambientColor = new Vector3(0.3f, 0.3f, 0.7f);
	
	final String finalPixelShader =  Gdx.files.internal("shaders/pixelShader.glsl").readString();
	final String vertexShader = Gdx.files.internal("shaders/vertexShader.glsl").readString();
	final String defaultPixelShader = Gdx.files.internal("shaders/defaultPixelShader.glsl").readString();
	
	private Texture light;
	private FrameBuffer fbo;
	//EOF Shaders
	
	public Play(Main game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		world = new World(new Vector2(0, 0f), true);
		world.setContactListener(new MyContactListener(this));
		
		player = new Player(world, 6, 22); //Create player
		bulletPool = new BulletPool(world);
		bulletsToBeRemoved = new Array<Bullet>();
		activeBullets = new Array<Bullet>();
		shootPos = new Vector2();
		
		TiledMap map = new TmxMapLoader().load("maps/test.tmx");
		Box2DMapObjectParser parser = new Box2DMapObjectParser(0.03125f);
		parser.load(world, map);
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale());
		box2dRenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();
		
		ShaderProgram.pedantic = false;
		defaultShader = new ShaderProgram(vertexShader, defaultPixelShader);
		finalShader = new ShaderProgram(vertexShader, finalPixelShader);
		
		finalShader.begin();
		finalShader.setUniformi("u_lightmap", 1);
		finalShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);
		finalShader.end();
		
		light = new Texture("shaders/light.png");
		
		/*////TESTING remove after
		//Lighting
		RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
        rayHandler.setCulling(true);                
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(1);
        rayHandler.setShadows(true);
   
		pointlight = new PointLight(rayHandler, 16);
		pointlight.setColor(1, 1, 1, 1);
		pointlight.attachToBody(player.getBody());
		pointlight.setDistance(2);
		
		
		Pixmap pixmap = new Pixmap( 32, 32, Format.RGBA8888 );
		pixmap.setColor( 1f, 1f, 1f, 1f );
		pixmap.fillRectangle(0, 0, 32, 32);
		testBg = new Texture(pixmap);
		*/
		//createBulletTrail();
		createInputProcessor();	
	}
	/*
	private void createBulletTrail() {
		Pixmap pixmap = new Pixmap( 32, 32, Format.RGBA8888 );
		pixmap.setColor( 0.41f, 0.78f, 1f, 1f );
		pixmap.fillRectangle(0, 0, 32, 32);
		bulletTrail = new Sprite(new Texture(pixmap));
		pixmap.dispose();
	}
	*/
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
			
		/*
		game.sb.begin();
		game.sb.draw(testBg, -Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    game.sb.end();
		
		rayHandler.setCombinedMatrix(game.cam.combined);
		rayHandler.updateAndRender();
		*/
		fbo.begin();
			game.sb.setProjectionMatrix(game.cam.combined);
			game.sb.setShader(defaultShader);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			game.sb.begin();
			game.sb.draw(light, 6, 22, 5, 5);
			game.sb.end();
		fbo.end();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.sb.setProjectionMatrix(game.cam.combined);
		game.sb.setShader(finalShader);
		fbo.getColorBufferTexture().bind(1); //this is important! bind the FBO to the 2nd texture unit
		light.bind(0); //we force the binding of a texture on first texture unit to avoid artefacts
					   //this is because our default and ambiant shader dont use multi texturing...
					   //you can basically bind anything, it doesnt matter
		
		FrameBuffer.unbind();
		
		player.render(game.sb, delta);
		mapRenderer.setView(game.cam);
		mapRenderer.render();

		box2dRenderer.render(world, game.cam.combined);
		
		//Render bullet trail
		shapeRenderer.setProjectionMatrix(game.cam.combined);
		shapeRenderer.setColor( 0.41f, 0.78f, 1f, 1f );
		shapeRenderer.begin(ShapeType.Filled);
		for(Bullet b : activeBullets){
			Vector2 bulletpos = b.getBody().getPosition();
			if(b.getCollisionPoints().size == 0) {	// no collision points
				shapeRenderer.rectLine(b.getShootingPoint(), bulletpos, Constants.WAVE_WIDTH);
			} else {
				shapeRenderer.rectLine(b.getShootingPoint(), b.getCollisionPoints().first(), Constants.WAVE_WIDTH); // 1 or more collision points
				if(b.getCollisionPoints().size > 1) {	//More than 1 collision points
					for(int i=0; i<b.getCollisionPoints().size-1; i++) {
						shapeRenderer.rectLine(b.getCollisionPoints().get(i), b.getCollisionPoints().get(i+1), Constants.WAVE_WIDTH);
					}
					shapeRenderer.rectLine(b.getCollisionPoints().get(b.getCollisionPoints().size - 1), bulletpos, Constants.WAVE_WIDTH);	// always draw last line to bullet position
				} else {
					shapeRenderer.rectLine(b.getCollisionPoints().first(), bulletpos, Constants.WAVE_WIDTH); // always draw last line to bullet position
				}
			}
		}
		shapeRenderer.end();
		
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.sb.begin();
			font.getData().setScale(1/64f);
			font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), Main.VIRTUAL_WIDTH / game.PPM * 0.94f, Main.VIRTUAL_HEIGHT / game.PPM * 0.99f);
		game.sb.end();
		
	}

	private void update(float delta) {
		world.step(1/60f, 8, 3);
		player.move();
		checkIfBulletsToBeRemoved();
		updateCamera();
	}

	private void checkIfBulletsToBeRemoved() {
		Iterator<Bullet> i = bulletsToBeRemoved.iterator();
		while(i.hasNext()) {
			Bullet bullet = i.next();
			world.destroyBody(bullet.getBody());
			activeBullets.removeValue(bullet, false);
			bulletPool.free(bullet);
			System.out.println("Body destroyed");
		}
		bulletsToBeRemoved.clear();
	}

	private void updateCamera() {
		Vector2 playerpos = player.getBody().getPosition();
		game.cam.position.set(playerpos.x, playerpos.y, 0);
		game.cam.update();
		
		/* In case needed, keeps the camera inside world borders */
		/*
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
		*/
		
	}

	@Override
	public void resize(int width, int height) {
		game.cam.setToOrtho(false, width / game.PPM, height / game.PPM);
		game.cam.update();
		
		fbo = new FrameBuffer(Format.RGBA8888, width, height, false);

		finalShader.begin();
		finalShader.setUniformf("resolution", width, height);
		finalShader.end();
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
		finalShader.dispose();
		fbo.dispose();
		world.dispose();
		mapRenderer.dispose();
		box2dRenderer.dispose();
		player.dispose();
		rayHandler.dispose();
	}

	public void addBulletToBeRemoved(Bullet bullet) {
		bulletsToBeRemoved.add(bullet);
		System.out.println("Added bullet");
	}

}
