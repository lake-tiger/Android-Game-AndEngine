package com.leo.andengine.chapter3;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.graphics.Typeface;
import android.widget.Toast;

public class CreatingButtons extends BaseGameActivity {

	public static int WIDTH = 800;
	public static int HEIGHT = 480;

	private Scene mScene;
	private Camera mCamera;
	private Font mFont;

	/* The ButtonSprite class requires us to use an ITiledTextureRegion object */
	private ITiledTextureRegion mButtonTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {

		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
	    
	    mFont = FontFactory.create(mEngine.getFontManager(),
                mEngine.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32f, true,
                Color.WHITE_ABGR_PACKED_INT);
        mFont.load();
        
	    
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

//		mTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(pBuildableBitmapTextureAtlas, pAssetManager, pAssetPath); 
		/* Create the bitmap texture atlas.
		 * 
		 * The bitmap texture atlas is created to fit a texture region of 300x50 pixels*/
		BuildableBitmapTextureAtlas bitmapTextureAtlas = new BuildableBitmapTextureAtlas(
				mEngine.getTextureManager(), 300, 50);

		/* Create the buttons texture region with 2 columns, 1 row */
		mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(bitmapTextureAtlas, getAssets(),
						"button_tiles.png", 2, 1);

		/* Build the bitmap texture atlas */
		try {
			bitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, 0));
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		/* Load the bitmap texture atlas */
		bitmapTextureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();
		
		/* Enable touch area binding on the mScene object */
		mScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

//	    final int maxUnlockedLevel = 7;
//	    final int levelSelectorChapter = 1;
//	    final int cameraWidth = WIDTH;
//	    final int cameraHeight = HEIGHT;
//	    
//	    LevelSelector levelSelector = new LevelSelector(maxUnlockedLevel, levelSelectorChapter, cameraWidth, cameraHeight, mScene, mEngine);
//	    levelSelector.createTiles(mTextureRegion, mFont);
//	    levelSelector.show(); 
	    
	    
		/* Create the buttonSprite object in the center of the Scene */
		ButtonSprite buttonSprite = new ButtonSprite(WIDTH * 0.5f,
				HEIGHT * 0.5f, mButtonTextureRegion,
				mEngine.getVertexBufferObjectManager()) {
			
			/* Override the onAreaTouched() event method */
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				/* If buttonSprite is touched with the finger */
				if(pSceneTouchEvent.isActionDown()){
					
					/* When the button is pressed, we can create an event 
					 * In this case, we're simply displaying a quick toast */
					CreatingButtons.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Button Pressed!", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				/* In order to allow the ButtonSprite to swap tiled texture region 
				 * index on our buttonSprite object, we must return the super method */
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
	
		/* Register the buttonSprite as a 'touchable' Entity */
//		mScene.registerTouchArea(buttonSprite);
		/* Attach the buttonSprite to the Scene */
//		mScene.attachChild(buttonSprite);
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}