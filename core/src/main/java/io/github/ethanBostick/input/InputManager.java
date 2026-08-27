package io.github.ethanBostick.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;

public class InputManager{
	private InputMultiplexer multiplexer;

	//make a singleton
	public InputManager(InputAdapter adapter){
		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(0,adapter); //the ui stage added first (typically)
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

	public void poll(){
		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			System.out.println("W");
		}
	}
}
