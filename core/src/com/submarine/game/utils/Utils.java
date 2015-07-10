package com.submarine.game.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
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
			verticesToWorldSize[i] = polyline.getTransformedVertices()[i] * 1/32f;
		}
		
        polyline.setVertices(verticesToWorldSize);
        
		return polyline;
	}
	
	public static Circle scaleDownShape2D(CircleMapObject circleMapObject) {
		Circle circle = ((CircleMapObject) circleMapObject).getCircle();
		circle.radius 	= circle.radius * 1/Main.PPM;
		circle.x 		= circle.x * 1/Main.PPM;
		circle.y 		= circle.y * 1/Main.PPM;
		
		return circle;
	}
	
	public static Ellipse scaleDownShape2D(EllipseMapObject ellipseMapObject) {
		Ellipse ellipse = ((EllipseMapObject) ellipseMapObject).getEllipse();

		ellipse.width 	= ellipse.width * 1/Main.PPM;
		ellipse.height 	= ellipse.height * 1/Main.PPM;
		ellipse.x 		= ellipse.x * 1/Main.PPM;
		ellipse.y 		= ellipse.y * 1/Main.PPM;
		
		return ellipse;
	}
	
	public static Polygon scaleDownShape2D(PolygonMapObject polygonMapObject) {
		Polygon polygon = ((PolygonMapObject) polygonMapObject).getPolygon();
		
		float[] verticesToWorldSize = new float[polygon.getVertices().length]; //Scale down vertices
        for(int i=0; i<polygon.getVertices().length; i++) {
			verticesToWorldSize[i] = polygon.getTransformedVertices()[i] * 1/32f;
		}
		polygon.setVertices(verticesToWorldSize);
		
		return polygon;
	}
	
	public static Rectangle scaleDownShape2D(RectangleMapObject rectangleMapObject) {
		Rectangle rectangle = ((RectangleMapObject) rectangleMapObject).getRectangle();

		rectangle.width 	= rectangle.width * 1/Main.PPM;
		rectangle.height 	= rectangle.height * 1/Main.PPM;
		rectangle.x 		= rectangle.x * 1/Main.PPM;
		rectangle.y 		= rectangle.y * 1/Main.PPM;
		
		return rectangle;
	}
	
}
