package com.submarine.game.utils;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.submarine.resourses.Bullet;

public class BulletPool extends Pool<Bullet>{

	private World world;
	
	public BulletPool(World world) {
		super(20);
		this.world = world;
	}
	
	@Override
	protected Bullet newObject() {
		//System.out.println("New bullet created");
		return new Bullet(world, 0, 0);
	}

}
