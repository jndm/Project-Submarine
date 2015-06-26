package com.submarine.game.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class Utils {
	public static Animation createAnimation(TextureAtlas atlas, String regionName, int frames, float frametime) {
		TextureRegion[] tr = new TextureRegion[frames];
		for(int i=0; i<frames; i++) {
			tr[i] = atlas.findRegion(regionName+i);
		}
		Animation a = new Animation(frametime, tr);
		return a;
	}
}
