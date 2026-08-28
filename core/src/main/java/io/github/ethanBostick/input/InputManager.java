package io.github.ethanBostick.input;

import io.github.ethanBostick.core.CameraController;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.InputMultiplexer;

public class InputManager{
	private InputMultiplexer multiplexer;
	private CameraController cameraController;

	//make a singleton
	public InputManager(InputAdapter adapter){
		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(0,adapter); //the ui stage added first (typically)
	}

	public void setupCameraController(int width, int height, OrthographicCamera camera){
		this.cameraController = new CameraController(width, height, camera);
	}

	public void resizeCameraController(int width, int height){
		this.cameraController.resize(width, height);;
	}

	public void addProcessor(InputProcessor p){
		this.multiplexer.addProcessor(p);
	}

	public void addProcessorAt(int index, InputProcessor p){
		this.multiplexer.addProcessor(index,p);
	}

	public void removeProcessor(int index){
		this.multiplexer.removeProcessor(index);
	}

	public void removeAdapter(InputAdapter adapter){
		this.multiplexer.removeProcessor(adapter); // remove the exact instance of passed from multiplexer
	}

	public InputMultiplexer	getMultiplexer(){
		return this.multiplexer;
	}

	public void poll(float delta){
		this.cameraController.handleKeyboardInput(delta);
		this.cameraController.update(delta);
	}
}
