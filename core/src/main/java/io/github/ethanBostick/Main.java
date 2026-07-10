package io.github.ethanBostick;

// Input
import io.github.ethanBostick.input.InputRouter;
import io.github.ethanBostick.input.MenuInputAdapter;
import io.github.ethanBostick.input.MapInputAdapter;
import io.github.ethanBostick.input.PopupInputAdapter;
// UI
import io.github.ethanBostick.ui.GameHUD;

//GDX stuff
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private SpriteBatch batch;
    private Texture image;

	//temp testing
	private Stage stage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

		//init basic input router
		InputRouter theInputRouter = new InputRouter(new MenuInputAdapter());
		Gdx.input.setInputProcessor(theInputRouter.getMultiplexer());

		//init ui systems
		GameHUD theGameHUD = new GameHUD();
		theInputRouter.addProcessorAt(0,theGameHUD.stage);
		this.stage = theGameHUD.stage;
    }

    @Override
    public void render() {
		super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
