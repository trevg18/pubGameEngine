package engineTester;

import java.util.*; 
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {	
		
		DisplayManager.createDisplay();	//Creates a new display window
		Loader loader = new Loader();	//Creates a new Loader object for accessing functions

		
		RawModel model = OBJLoader.loadObjModel("stall", loader);
		RawModel dragonModel = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragonTextured = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("victoryBlue")));
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("stallTexture")));
		ModelTexture texture = staticModel.getTexture();
		//texture.setReflectivity(1);
		//texture.setShineDamper(10);
		
		Entity entityDragon = new Entity(dragonTextured, new Vector3f(0,-4.0f, -25), 0,0,0,1);
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-25),0,160,0,1);
		Light light = new Light(new Vector3f(3000,2000,20), new Vector3f(1,1,1));
		Camera camera = new Camera();
		Random random = new Random();
		Terrain terrain = new Terrain(0,0,loader,new ModelTexture(loader.loadTexture("grass")));
		Terrain terrain2 = new Terrain(1,0,loader,new ModelTexture(loader.loadTexture("grass")));
		List<Entity> theDragons = new ArrayList<Entity>();
		for(int i = 0; i < 50; i++){
			float x = random.nextFloat() * 100 - 50;
			float y = random.nextFloat() * 100 - 50;
			float z = random.nextFloat() * -300;
			theDragons.add(new Entity(dragonTextured, new Vector3f(x,y,z), random.nextFloat()* 180f,
			random.nextFloat() * 180f , 0f , 1f));
		}
		
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
			//entityDragon.increaseRotation(0, 1, 0);
			camera.move();

			for(Entity e : theDragons){
				renderer.processEntity(e);
			}
			
			renderer.processEntity(entityDragon);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			//renderer.render(entity,shader);
			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
