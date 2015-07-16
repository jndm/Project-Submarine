package com.submarine.resources;

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
	
	public Bullet(World world, float x, float y) {
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
		
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
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
		ricochetCount = 0;
		collisionPoints.clear();
	}

}
