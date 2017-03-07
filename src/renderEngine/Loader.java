package renderEngine;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {
	
	// ArrayLists for memory management and clean up functions
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	/*
	 * Takes in arrays containing positional data, texture coordinates, and indices.
	 * Creates a new VAO and stores VBOS containing positions and texture coordinates
	 * in the VAO'S attribute lists. Also configures indices. 
	 */
	
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords,float[] normals, int[] indices){
		int vaoID = createVAO();	// Creates new VAO object and binds VAO
		bindIndicesBuffer(indices); // Loads in indices
		storeDataInAttributeList(0,3, positions); // Stores positional data in VAO attribute list 0
		storeDataInAttributeList(1,2, textureCoords); // Stores texture data in VAO attribute list 1
		storeDataInAttributeList(2,3, normals); //Stores normal values in VAO attribute list 2
		unbindVAO(); // Unbinds VAO
		return new RawModel(vaoID, indices.length); // Returns new RawModel object with vaoID and indices length
	}
	
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName+".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	/*
	 * Takes in an attribute number specifying the attribute list in the VAO 
	 * in which the data will be stored. Coordinate size represents the 
	 * number of floating point values (components) per vertex. In the case of 
	 * 3D rendering, this value will be 3 for the positional array and 2 for the 2D
	 * textures array. 
	*/
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();	// Create new VBO and store ID
		vbos.add(vboID);	// Add to VBOS ArrayList for clean up later
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // Bind buffer (make active)
		FloatBuffer buffer = storeDataInFloatBuffer(data); // Create float buffer with data array
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);	//Puts the data from the newly created buffer into the active VBO 
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize , GL11.GL_FLOAT, false, 0, 0); //Puts the initialized VBO into the VAO attribute list
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Unbinds the VBO (make inactive)
	}
	
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0); // Unbinds current VAO
	}
	
	
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}

}
