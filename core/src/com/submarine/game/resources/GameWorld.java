package com.submarine.game.resources;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.submarine.game.Main;
import com.submarine.game.screens.Play;
import com.submarine.game.utils.Box2DMapObjectParserHelper;
import com.submarine.game.utils.Constants;
import com.submarine.game.utils.Utils;

public class GameWorld {
	
	private Main game;
	private Play play;
	private World world;
	private Level level;
	
	private Box2DDebugRenderer box2dRenderer;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	private Array<Shape2D> walls;
	private Vector2 spawnpoint;
	private Ellipse goal;
	
	public GameWorld(Play play, Main game, World world, Level level) {	
		this.game = game;
		this.world = world;
		this.play = play;
		this.level = level;
		
		TiledMap map = new TmxMapLoader().load("maps/"+level.getName()+".tmx");
		Box2DMapObjectParserHelper parser = new Box2DMapObjectParserHelper(1 / Constants.PPM);
		try {
			parser.load(world, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale(), game.sb);
		box2dRenderer = new Box2DDebugRenderer();
	
		MapObjects mapObjects = map.getLayers().get("Walls").getObjects();
		walls = new Array<Shape2D>();
		
		for(MapObject mo : mapObjects) {	
			if(mo instanceof CircleMapObject) {
				walls.add(Utils.scaleDownShape2D((CircleMapObject) mo));
			} else if(mo instanceof EllipseMapObject) {
				walls.add(Utils.scaleDownShape2D((EllipseMapObject) mo));	
			} else if(mo instanceof PolygonMapObject) {
				walls.add(Utils.scaleDownShape2D((PolygonMapObject) mo));
			} else if(mo instanceof PolylineMapObject) {
				walls.add(Utils.scaleDownShape2D((PolylineMapObject) mo));
			} else if(mo instanceof RectangleMapObject) {
				walls.add(Utils.scaleDownShape2D((RectangleMapObject) mo));
			}		
		}
		
		//Handle spawn & goal point creating
		Ellipse spawnObject = Utils.scaleDownShape2D((EllipseMapObject) map.getLayers().get("Points").getObjects().get("Spawn"));
		spawnpoint = new Vector2(spawnObject.x + spawnObject.width/2, spawnObject.y + spawnObject.height/2);
		
		goal = Utils.scaleDownShape2D((EllipseMapObject) map.getLayers().get("Points").getObjects().get("Goal"));
		
	}

	public void render() {
		mapRenderer.setView(game.cam);
		mapRenderer.render();
		//box2dRenderer.render(world, game.cam.combined);
		
		game.shapeRenderer.setProjectionMatrix(game.cam.combined);
		game.shapeRenderer.setColor(play.getCurrentThemeColor());
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
		game.shapeRenderer.ellipse(goal.x, goal.y, goal.width, goal.height);
		game.shapeRenderer.end();
	}
	
	public void dispose() {
		mapRenderer.dispose();
		box2dRenderer.dispose();
	}

	public Vector2 getSpawnpoint() {
		return spawnpoint;
	}

	public Ellipse getGoal() {
		return goal;
	}

}
