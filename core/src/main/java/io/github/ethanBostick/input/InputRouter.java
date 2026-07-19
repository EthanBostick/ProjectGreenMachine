package io.github.ethanBostick.input;

import io.github.ethanBostick.input.MenuInputAdapter;
import io.github.ethanBostick.input.MapInputAdapter;
import io.github.ethanBostick.input.PopupInputAdapter;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;

//ui stage
//import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputRouter{
	private InputMultiplexer multiplexer;

	//make a singleton probably
	public InputRouter(InputAdapter adapter){
		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(0,adapter);
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
}
