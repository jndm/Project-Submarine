package com.submarine.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.submarine.game.screens.Play;
import com.submarine.resourses.Bullet;
import com.submarine.resourses.Player;

public class MyContactListener implements ContactListener{
	
	private Play play;
	
	public MyContactListener(Play play) {
		this.play = play;
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		//Player handling
		if(fa.getUserData() != null && fa.getUserData() instanceof Player) {
			Player p = (Player) fa.getUserData();
			p.setTakeDamage(true);
			p.removeHp();
		} else if(fb.getUserData() != null && fb.getUserData() instanceof Player) {
			Player p = (Player) fb.getUserData();
			p.setTakeDamage(true);
			p.removeHp();
		}
	
		
		//Bullet handling
		if(fa.getUserData() != null && fa.getUserData() instanceof Bullet) { //If fixture a = bullet and fb = wall
			Bullet b = (Bullet) fa.getUserData();
			b.addRicochetCount();
			b.addCollisionPoint(new Vector2(b.getBody().getWorldCenter()));
			if(b.shouldBeRemoved()) {
				play.addBulletToBeRemoved(b);
			}
		} else if(fb.getUserData() != null && fb.getUserData() instanceof Bullet) { //If fixture a = bullet and fb = wall
			Bullet b = (Bullet) fb.getUserData();
			b.addRicochetCount();
			b.addCollisionPoint(new Vector2(b.getBody().getWorldCenter()));
			if(b.shouldBeRemoved()) {
				play.addBulletToBeRemoved(b);
			}
		}

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
