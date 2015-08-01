package com.submarine.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.submarine.game.utils.Constants;

public class Bullet implements Poolable{
	
	private Body body;
	private BodyDef bodyDef;
	private Fixture fixture;
	private FixtureDef fixtureDef;
	
	private int ricochetCount = 0;
	private int maxRicochet = 5;
	private Array<Vector2> collisionPoints;
	private Vector2 shootingPoint;
	
	private ParticleEffect beam;
	
	private Color currentThemeColor;
	private boolean bodyRemoved = false;
	
	public Bullet(World world, float x, float y, Color currentThemeColor) {
		this.currentThemeColor = currentThemeColor;
		
		collisionPoints = new Array<Vector2>();
		shootingPoint = new Vector2();
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);

		fixtureDef = new FixtureDef();
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 1f;
		fixtureDef.filter.categoryBits = Constants.BULLET_CATEGORY;
		fixtureDef.filter.maskBits = Constants.BULLET_MASK;
		
		//Beam particle-effect
		beam = new ParticleEffect();
        beam.load(Gdx.files.internal("effects/beam.p"), Gdx.files.internal("effects"));

        float[] color = { currentThemeColor.r, currentThemeColor.g, currentThemeColor.b };
		beam.getEmitters().get(0).getTint().setColors(color);
        beam.start();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
		beam.setPosition(x, y);	
	}
	
	public void setVelocity(float x, float y) {
		Vector2 velocity = new Vector2();
        velocity.set(x - body.getPosition().x, y - body.getPosition().y);
        Vector2 normal = velocity.nor();
        
        /* Nice print for debugging 
        System.out.println("\nPlayer pos x: "+body.getPosition().x+ " y: "+body.getPosition().y+"\n"+
        "Clicked at x: "+x+ " y: "+y+"\n"+
        "Normal: x: "+normal.x+" y: "+normal.y+"\n"+
        "Body world center: x: "+body.getWorldCenter().x+" y: "+body.getWorldCenter().y+"\n");
        */
        body.applyLinearImpulse(normal.scl(0.5f), body.getWorldCenter(), true);
	}
	
	public Body getBody() {
		return body;
	}

	public void addRicochetCount() {
		ricochetCount += 1;
	}
	
	public boolean shouldBeRemoved() {
		return maxRicochet <= ricochetCount;
	}

	public void addToWorld(World world) {
		//Set shape here so dispose can be called anytime new bullet is created
		CircleShape circle = new CircleShape();
		circle.setRadius(0.1f);
		fixtureDef.shape = circle;
		
		body = world.createBody(bodyDef);
		body.setBullet(true);
		body.setFixedRotation(true);
		
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
		
		circle.dispose();
	}
	
	public void render(SpriteBatch sb, float delta) {
		beam.draw(sb, delta);
	}
	
	public void addCollisionPoint(Vector2 cp) {
		collisionPoints.add(cp);
	}
	
	public Array<Vector2> getCollisionPoints() {
		return collisionPoints;
	}
	
	public Vector2 getShootingPoint() {
		return shootingPoint;
	}
	
	public void setShootingPoint(Vector2 sp) {
		shootingPoint.set(sp);
		//System.out.println("Adding point shooting point: ("+shootingPoint.x+", "+shootingPoint.y+")");
	}
	
	public float getAngle() {
		return body.getLinearVelocity().angle();
	}

	@Override
	public void reset() {
		bodyRemoved = false;
		ricochetCount = 0;
		collisionPoints.clear();
		beam.getEmitters().get(0).reset();
	}

	public void updateBulletTrail() {
		Vector2 bulletpos = body.getPosition();
		beam.setPosition(bulletpos.x, bulletpos.y);
        
        for(ParticleEmitter emitter : beam.getEmitters()) {
			emitter.getRotation().setLow(getAngle());
			emitter.getRotation().setHigh(getAngle());
		}
	}
	
	public boolean isParticleEffectComplete() {
		return beam.isComplete();
	}

	public void dispose() {
		beam.dispose();
	}

	public void setBodyRemoved(boolean b) {
		bodyRemoved  = b;
	}
	
	public boolean isBodyRemoved() {
		return bodyRemoved;
	}

	public void allowParticleCompletion() {
		beam.allowCompletion();
	}
}
