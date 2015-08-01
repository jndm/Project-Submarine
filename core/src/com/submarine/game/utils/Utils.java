package com.submarine.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.submarine.game.Main;

public final class Utils {
	
	public static Animation createAnimation(TextureAtlas atlas, String regionName, int frames, float frametime) {
		TextureRegion[] tr = new TextureRegion[frames];
		for(int i=0; i<frames; i++) {
			tr[i] = atlas.findRegion(regionName+i);
		}
		Animation a = new Animation(frametime, tr);
		return a;
	}
	
	public static Polyline scaleDownShape2D(PolylineMapObject polylineMapObject) {
		Polyline polyline = ((PolylineMapObject) polylineMapObject).getPolyline();
		
		float[] verticesToWorldSize = new float[polyline.getVertices().length]; //Scale down vertices
        for(int i=0; i<polyline.getVertices().length; i++) {
			verticesToWorldSize[i] = polyline.getTransformedVertices()[i] * 1/Constants.PPM;
		}
		
        polyline.setVertices(verticesToWorldSize);
        
		return polyline;
	}
	
	public static Circle scaleDownShape2D(CircleMapObject circleMapObject) {
		Circle circle = ((CircleMapObject) circleMapObject).getCircle();
		circle.radius 	= circle.radius * 1/Constants.PPM;
		circle.x 		= circle.x * 1/Constants.PPM;
		circle.y 		= circle.y * 1/Constants.PPM;
		
		return circle;
	}
	
	public static Ellipse scaleDownShape2D(EllipseMapObject ellipseMapObject) {
		Ellipse ellipse = ((EllipseMapObject) ellipseMapObject).getEllipse();

		ellipse.width 	= ellipse.width * 1/Constants.PPM;
		ellipse.height 	= ellipse.height * 1/Constants.PPM;
		ellipse.x 		= ellipse.x * 1/Constants.PPM;
		ellipse.y 		= ellipse.y * 1/Constants.PPM;
		
		return ellipse;
	}
	
	public static Polygon scaleDownShape2D(PolygonMapObject polygonMapObject) {
		Polygon polygon = ((PolygonMapObject) polygonMapObject).getPolygon();
		
		float[] verticesToWorldSize = new float[polygon.getVertices().length]; //Scale down vertices
        for(int i=0; i<polygon.getVertices().length; i++) {
			verticesToWorldSize[i] = polygon.getTransformedVertices()[i] * 1/Constants.PPM;
		}
		polygon.setVertices(verticesToWorldSize);
		
		return polygon;
	}
	
	public static Rectangle scaleDownShape2D(RectangleMapObject rectangleMapObject) {
		Rectangle rectangle = ((RectangleMapObject) rectangleMapObject).getRectangle();

		rectangle.width 	= rectangle.width * 1/Constants.PPM;
		rectangle.height 	= rectangle.height * 1/Constants.PPM;
		rectangle.x 		= rectangle.x * 1/Constants.PPM;
		rectangle.y 		= rectangle.y * 1/Constants.PPM;
		
		return rectangle;
	}
	
	/**
	 * Returns bitmapfont created from ttf file. 
	 * <p>
	 *
	 * @param  file an absolute path to .ttf file
	 * @param  size fontsize
	 * @return BitmapFont
	 */
	public static BitmapFont createFont(String file, float size) {
		FileHandle fontFile = Gdx.files.internal(file);
	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
	    
	    FreeTypeFontParameter parameter = new FreeTypeFontParameter();    
	    parameter.size = (int)Math.ceil(size);
	    
	    parameter.minFilter = TextureFilter.Nearest;
	    parameter.magFilter = TextureFilter.MipMapLinearNearest;
	    
	   
	    BitmapFont textFont = generator.generateFont(parameter);

	    generator.dispose();
	    return textFont;
	}
	
}
