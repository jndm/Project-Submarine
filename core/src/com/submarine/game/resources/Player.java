package com.submarine.game.resources;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.screens.Play;
import com.submarine.game.utils.Constants;

public class Player {
	
	private Play play;
	
	//Body, fixture, sprite and animations
	private Body body;
	private Array<Fixture> activeFixture;
	private Array<FixtureDef> fixtureDefs;
	private Sprite sprite;
	private Array<Vector2[]> rightVertices, leftVertices;
	private AnimatedBox2DSprite takeDamageAnimation;
	private boolean takeDamage = false;
	
	//Submarine stats
	private Vector2 movement;
	private float speed = 10;
	private float hp = 2;
	
	// Key hold down
	private boolean right = false, left = false, up = false, down = false;
	
	// Bubble particle effect
	private ParticleEffect bubbles;
	
	/*
	private ParticleEffectPool particlePool;
	private Array<PooledEffect> effects;
	private float particleTimer = 999f;
	private float particleTimeLimit = 0.07f;
	
	private Vector2 bubblePosition;
	*/
	public Player(World world, Vector2 spawnpoint, Play play) {
		this.play = play;
		
		movement = new Vector2(0, 0);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnpoint.x, spawnpoint.y);
		
		createPlayerFixtures(world, bodyDef);	
		
		//Create sprite for player
		sprite = new Box2DSprite(new Texture(Gdx.files.internal("player/submarine.normal.png")));
		sprite.setSize(sprite.getTexture().getWidth() / Constants.PPM, sprite.getTexture().getHeight() / Constants.PPM);
		sprite.setColor(play.getCurrentThemeColor());
			
		//TEMPORARY SOLUTION * CHANGE TO ASSETMANAGER AT SOME POINT	
		TextureRegion[] txr = new TextureRegion[]  
		{
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.normal.png"))), 
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.flicker.png"))), 
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.normal.png"))), 
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.flicker.png"))), 
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.normal.png"))), 
			new TextureRegion(new Texture(Gdx.files.internal("player/submarine.flicker.png"))),
		};
		
		takeDamageAnimation = new AnimatedBox2DSprite(new AnimatedSprite(new Animation(1/6f, txr)));
		takeDamageAnimation.setSize(takeDamageAnimation.getWidth() / Constants.PPM, takeDamageAnimation.getHeight() / Constants.PPM);
		
		//Particle-effect
		bubbles = new ParticleEffect();
		bubbles.load(Gdx.files.internal("effects/bubbles.p"), Gdx.files.internal("effects"));
		bubbles.start();
		
		float[] color = { play.getCurrentThemeColor().r, play.getCurrentThemeColor().g, play.getCurrentThemeColor().b };
		bubbles.getEmitters().get(0).getTint().setColors(color);

	}

	public void render(SpriteBatch sb, float delta) {
		sb.begin();
		if(takeDamage) {
			takeDamageAnimation.update();
			if(takeDamageAnimation.getAnimation().getKeyFrameIndex(takeDamageAnimation.getTime()) % 2 == 0) { //Tint every other frame with theme color
				takeDamageAnimation.setColor(play.getCurrentThemeColor());
			} else {
				takeDamageAnimation.setColor(Constants.WHITE);
			}
			takeDamageAnimation.draw(sb);		
			if(takeDamageAnimation.isAnimationFinished()) {
				takeDamage = false;
				takeDamageAnimation.setTime(0);
			}			
		} else {
			sprite.draw(sb);
		}
		
		bubbles.draw(sb, delta);
		sb.end();
		//Gdx.app.log("pool stats", "active: " + effects.size + " | free: " + particlePool.getFree() + "/" + particlePool.max + " | record: " + particlePool.peak);
	}
	
	public Body getBody() {
		return body;
	}
	
	public void move() {		
		movement.set(0, 0);
		
		//Horizontal
		if(left)  { movement.x -= speed; } 	
		if(right) { movement.x += speed; } 
		
		//Vertical
		if(up) { movement.y += speed; } 
		if(down) { movement.y -= speed; }
		
		updateBubbles();
		
		movement.clamp(-speed, speed);	//Clamp movement to stay in range -speed to speed
		body.applyForceToCenter(movement, true); // Apply movement
		
		//If turned around horizontally, flip sprite & animations & fixtures
		sprite.setOrigin(body.getWorldCenter().x, body.getWorldCenter().x);
		if(movement.x > 0 && sprite.isFlipX()) {	
			sprite.flip(true, false);
			takeDamageAnimation.flipFrames(true, false);
			swapFixture(false);	
		} else if(movement.x < 0 && !sprite.isFlipX()) {
			sprite.flip(true, false);
			takeDamageAnimation.flipFrames(true, false);
			swapFixture(true);	
		}
		sprite.setPosition(body.getPosition().x, body.getPosition().y);
		takeDamageAnimation.setPosition(body.getPosition().x, body.getPosition().y);
	}
	
	// param is true if turned going to left
	private void updateBubbles() {
		if(left || right || up || down) {	//If moving some where (moving key is down) add bubbles	
			bubbles.getEmitters().get(0).setContinuous(true);
			if(sprite.isFlipX()) { // going right
				bubbles.setPosition(body.getPosition().x + sprite.getWidth(), body.getPosition().y + sprite.getHeight() / 2);
			} else { //going left
				bubbles.setPosition(body.getPosition().x, body.getPosition().y + sprite.getHeight() / 2);
			}
		} else {
			bubbles.getEmitters().get(0).setContinuous(false); //stop bubbles
		}
	
	}

	// param is true if turned to left
	private void swapFixture(boolean facingLeft) {
		for(Fixture f : activeFixture) {
			body.destroyFixture(f);
		}
		activeFixture.clear();
		activeFixture.shrink();
		
		Array<Vector2[]> vertices = facingLeft ? leftVertices : rightVertices; 
		
		//Create fixtures
		for(int i=0; i<fixtureDefs.size; i++) {
			PolygonShape shape = new PolygonShape(); //Setup new shape from vertices array
			shape.set(vertices.get(i));
			fixtureDefs.get(i).shape = shape;		
			
			Fixture f = body.createFixture(fixtureDefs.get(i));	//Create fixture
			f.setUserData(this);
			activeFixture.add(f);
			
			shape.dispose(); //Dispose shape
		}
	}
	
	public void removeHp() {
		hp -= 1;
		takeDamage = true;
		System.out.println("Hp removed");
		if(hp == 0) {
			die();
		}
	}

	public void die() {
		// TODO What to do when player dead
		System.out.println("Player is dead!");
	}
	
	public void dispose() {
		sprite.getTexture().dispose();
		takeDamageAnimation.getAnimatedSprite().getTexture().dispose();
		bubbles.dispose();
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setTakeDamage(boolean takeDamage) {
		this.takeDamage = takeDamage;
	}
	
	private void createPlayerFixtures(World world, BodyDef bodyDef) {
		activeFixture = new Array<Fixture>();
		fixtureDefs = new Array<FixtureDef>();
		rightVertices = new Array<Vector2[]>();
		leftVertices = new Array<Vector2[]>();
		
		//Setup submarine right shape vertices:
		Vector2[] rightBodyVertices = new Vector2[8];
		rightBodyVertices[0] = new Vector2( 4, 16);	rightBodyVertices[1] = new Vector2( 9, 20);
		rightBodyVertices[2] = new Vector2(26, 20);	rightBodyVertices[3] = new Vector2(32, 15);
		rightBodyVertices[4] = new Vector2(32, 11);	rightBodyVertices[5] = new Vector2(25,  6);
		rightBodyVertices[6] = new Vector2(11,  6);	rightBodyVertices[7] = new Vector2( 4, 11);
		
		Vector2[] rightCabinVertices = new Vector2[6];
		rightCabinVertices[0] = new Vector2(15, 20); rightCabinVertices[1] = new Vector2(15, 23);
		rightCabinVertices[2] = new Vector2(16, 25); rightCabinVertices[3] = new Vector2(19, 25);
		rightCabinVertices[4] = new Vector2(21, 23); rightCabinVertices[5] = new Vector2(21, 20);
		
		Vector2[] rightPropellerVertices = new Vector2[7];
		rightPropellerVertices[0] = new Vector2( 1,  9); rightPropellerVertices[1] = new Vector2( 1, 17);
		rightPropellerVertices[2] = new Vector2( 2, 17); rightPropellerVertices[3] = new Vector2( 3, 17);
		rightPropellerVertices[4] = new Vector2( 4, 16); rightPropellerVertices[5] = new Vector2( 4, 10);
		rightPropellerVertices[6] = new Vector2( 3,  9);
		
		rightVertices.add(rightBodyVertices);
		rightVertices.add(rightPropellerVertices);
		rightVertices.add(rightCabinVertices);
		
		//Setup submarine left shape vertices
		Vector2[] leftBodyVertices = new Vector2[8];
		leftBodyVertices[0] = new Vector2( 0, 15); leftBodyVertices[1] = new Vector2( 5, 19);
		leftBodyVertices[2] = new Vector2(22, 20); leftBodyVertices[3] = new Vector2(29, 15);
		leftBodyVertices[4] = new Vector2(29, 11); leftBodyVertices[5] = new Vector2(21,  6);
		leftBodyVertices[6] = new Vector2( 7,  6); leftBodyVertices[7] = new Vector2( 0, 11);
		
		Vector2[] leftCabinVertices = new Vector2[6];
		leftCabinVertices[0] = new Vector2(12, 20);	leftCabinVertices[1] = new Vector2(12, 23);
		leftCabinVertices[2] = new Vector2(13, 25);	leftCabinVertices[3] = new Vector2(16, 25);
		leftCabinVertices[4] = new Vector2(18, 23);	leftCabinVertices[5] = new Vector2(18, 20);
		
		Vector2[] leftPropellerVertices = new Vector2[7];
		leftPropellerVertices[0] = new Vector2(32, 9);	leftPropellerVertices[1] = new Vector2(32, 17);
		leftPropellerVertices[2] = new Vector2(31, 17);	leftPropellerVertices[3] = new Vector2(29, 17);
		leftPropellerVertices[4] = new Vector2(28, 16);	leftPropellerVertices[5] = new Vector2(28, 10);
		leftPropellerVertices[6] = new Vector2(29, 9);
		
		leftVertices.add(leftBodyVertices);
		leftVertices.add(leftCabinVertices);
		leftVertices.add(leftPropellerVertices);
		
		//Scale vertices to world (aka. divide every vertice point with 32)
		for(int i=0; i<3; i++) {	
			for(int j=0; j<leftVertices.get(i).length; j++) {
				leftVertices.get(i)[j] = leftVertices.get(i)[j].scl(1 / Constants.PPM);
			}
			for(int j=0; j<rightVertices.get(i).length; j++) {
				rightVertices.get(i)[j] = rightVertices.get(i)[j].scl(1 / Constants.PPM);
			}
		}
		
		//Create shapes
		PolygonShape bodyShape = new PolygonShape();
		bodyShape.set(rightBodyVertices);
		
		PolygonShape cabinShape = new PolygonShape();
		cabinShape.set(rightCabinVertices);
		
		PolygonShape propellerShape = new PolygonShape();
		propellerShape.set(rightPropellerVertices);
		
		// Create fixtureDefs
		FixtureDef bodyFixtureDef = new FixtureDef();
		bodyFixtureDef.shape = bodyShape;		//set shape to right first
		bodyFixtureDef.density = 2f; 
		bodyFixtureDef.friction = 0.4f;
		bodyFixtureDef.restitution = 0.6f;
		bodyFixtureDef.filter.categoryBits = Constants.PLAYER_CATEGORY;
		bodyFixtureDef.filter.maskBits = Constants.PLAYER_MASK;
			
		FixtureDef cabinFixtureDef = new FixtureDef();
		cabinFixtureDef.shape = cabinShape;		//set shape to right first
		cabinFixtureDef.density = 0f; 
		cabinFixtureDef.friction = 0.4f;
		cabinFixtureDef.restitution = 0.6f;
		cabinFixtureDef.filter.categoryBits = Constants.PLAYER_CATEGORY;
		cabinFixtureDef.filter.maskBits = Constants.PLAYER_MASK;
		
		FixtureDef propellerFixtureDef = new FixtureDef();
		propellerFixtureDef.shape = propellerShape;		//set shape to right first
		propellerFixtureDef.density = 0f; 
		propellerFixtureDef.friction = 0.4f;
		propellerFixtureDef.restitution = 0.6f;
		propellerFixtureDef.filter.categoryBits = Constants.PLAYER_CATEGORY;
		propellerFixtureDef.filter.maskBits = Constants.PLAYER_MASK;
		
		//Add fixturedefs to array so they can be used again
		fixtureDefs.add(bodyFixtureDef);
		fixtureDefs.add(cabinFixtureDef);
		fixtureDefs.add(propellerFixtureDef);
		
		//Create body
		body = world.createBody(bodyDef);
		body.setLinearDamping(2f);
		body.setFixedRotation(true);
		
		//Create fixtures
		for(FixtureDef fd : fixtureDefs) {
			Fixture f = body.createFixture(fd);
			f.setUserData(this);
			activeFixture.add(f);
		}
		
		//dispose shapes
		bodyShape.dispose(); 
		cabinShape.dispose();
		propellerShape.dispose();
	}
	
	public float getPlayerCenterX() { 
		return body.getPosition().x + sprite.getWidth() / 2;
	}
	
	public float getPlayerCenterY() { 
		return body.getPosition().y + sprite.getHeight() / 2;
	}
}
