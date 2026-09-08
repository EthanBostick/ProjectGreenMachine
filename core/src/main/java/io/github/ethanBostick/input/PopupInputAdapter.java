package io.github.ethanBostick.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;

public class PopupInputAdapter extends InputAdapter{

	//reads mouse clicks
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		switch(button){
			case Input.Buttons.LEFT:
				break;
			case Input.Buttons.RIGHT:
				break;
		}

		return true;
	}

	//key read method
	@Override
	public boolean keyDown(int keycode){

		switch(keycode){
			case Input.Keys.W:
				break;
			case Input.Keys.S:
				break;
			case Input.Keys.A:
				break;
			case Input.Keys.D:
				break;
		}
		return true;
	}

	//scroll wheel reading
	@Override
	public boolean scrolled(float amountX, float amountY){

		if (amountY > 0){
			return true;
		}
		else if (amountY < 0){
			return true;
		}
		return false;
	}
}
