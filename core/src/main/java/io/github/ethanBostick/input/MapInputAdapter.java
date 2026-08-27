package io.github.ethanBostick.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;

public class MapInputAdapter extends InputAdapter{

	//reads mouse clicks
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.printf("%d,%d\n",screenX,screenY);
		System.out.printf("b: %d\n",button);

		switch(button){
			case Input.Buttons.LEFT:
				System.out.printf("left-click\n");
				break;
			case Input.Buttons.RIGHT:
				System.out.printf("right-click\n");
				break;
		}

		return true;
	}

	//key read method
	@Override
	public boolean keyDown(int keycode){

		switch(keycode){
			case Input.Keys.W:
				System.out.println("W down");
				break;
			case Input.Keys.S:
				System.out.println("S down");
				break;
			case Input.Keys.A:
				System.out.println("A down");
				break;
			case Input.Keys.D:
				System.out.println("D down");
				break;
		}
		return true;
	}

	//scroll wheel reading
	@Override
	public boolean scrolled(float amountX, float amountY){

		if (amountY > 0){
			System.out.printf("scrolled %f\n",amountY);
			return true;
		}
		else if (amountY < 0){
			System.out.printf("scrolled %f\n",amountY);
			return true;
		}
		return false;
	}
}
