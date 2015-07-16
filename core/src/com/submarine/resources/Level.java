package com.submarine.resources;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.utils.Utils;

public class Level {
	
	private Main game;
	private World world;
	
	private Box2DDebugRenderer box2dRenderer;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	private Array<Shape2D> walls;
	
	public Level(Main game, World world) {	
		this.game = game;
		this.world = world;
		
		TiledMap map = new TmxMapLoader().load("maps/test.tmx");
		Box2DMapObjectParser parser = new Box2DMapObjectParser(0.03125f);
		parser.load(world, map);
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale(), game.sb);
		box2dRenderer = new Box2DDebugRenderer();
		
		MapObjects mapObjects = map.getLayers().get("Walls").getObjects();
		walls = new Array<Shape2D>();
		
		for(MapObject mo : mapObjects) {	
			if(mo instanceof CircleMapObject) {
				walls.add(Utils.scaleDownShape2D((CircleMapObject) mo));
			} else if(mo instanceof EllipseMapObject) {
				walls.add(Utils.scaleDownShape2D((EllipseMapObject) mo));	//Not working with box2d
			} else if(mo instanceof PolygonMapObject) {
				walls.add(Utils.scaleDownShape2D((PolygonMapObject) mo));
			} else if(mo instanceof PolylineMapObject) {
				walls.add(Utils.scaleDownShape2D((PolylineMapObject) mo));
			} else if(mo instanceof RectangleMapObject) {
				walls.add(Utils.scaleDownShape2D((RectangleMapObject) mo));
			}		
		}
	}

	public void render() {
		//mapRenderer.setView(game.cam);
		//mapRenderer.render();
		//box2dRenderer.render(world, game.cam.combined);
		
		game.shapeRenderer.setProjectionMatrix(game.cam.combined);
		game.shapeRenderer.setColor(0.41f, 0.78f, 1f, 1f);
		game.shapeRenderer.begin(ShapeType.Line);
		for(Shape2D shape : walls) {
			if(shape instanceof Circle) {
				game.shapeRenderer.circle(((Circle) shape).x, ((Circle) shape).y, ((Circle) shape).radius);
			} else if(shape instanceof Ellipse) {
				game.shapeRenderer.ellipse(((Ellipse) shape).x, ((Ellipse) shape).y, ((Ellipse) shape).width, ((Ellipse) shape).height);
			} else if(shape instanceof Polygon) {
				game.shapeRenderer.polygon(((Polygon) shape).getVertices());
			} else if(shape instanceof Polyline) {
				game.shapeRenderer.polyline(((Polyline) shape).getVertices());				
			} else if(shape instanceof Rectangle) {
				game.shapeRenderer.rect(((Rectangle) shape).x, ((Rectangle) shape).y, ((Rectangle) shape).width, ((Rectangle) shape).height);
			}
		}
		game.shapeRenderer.end();
	}
	
	public void dispose() {
		mapRenderer.dispose();
		box2dRenderer.dispose();
	}
}
