package com.leo.andengine.chapter2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.math.MathUtils;

	/* This recipe requires touch input, so we will need to implement a touch listener */
public class RelativeRotation extends BaseGameActivity implements IOnSceneTouchListener{

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	
	private static final int DEFAULT_IMAGE_ROTATION = 90;
	
	private Scene mScene;
	private Camera mCamera;

	private Sprite mArrowSprite;
	private Sprite mMarbleSprite;

	private ITextureRegion mArrowTextureRegion;
	private ITextureRegion mMarbleTextureRegion;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		
		mArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "arrow.png");
		mMarbleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "marble.png");
		
		try {
			mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		mBitmapTextureAtlas.load();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();
		
		/* Register this activity as the mScene's touch listener */
		mScene.setOnSceneTouchListener(this);
		
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		/* Add our marble sprite to the bottom left side of the Scene initially */
		mMarbleSprite = new Sprite(mMarbleTextureRegion.getWidth(), mMarbleTextureRegion.getHeight(), mMarbleTextureRegion, mEngine.getVertexBufferObjectManager());
		
		/* Attach the marble to the Scene */
		mScene.attachChild(mMarbleSprite);
		
		/* Create the arrow sprite and center it in the Scene */
		mArrowSprite = new Sprite(WIDTH * 0.5f, HEIGHT * 0.5f, mArrowTextureRegion, mEngine.getVertexBufferObjectManager());
		
		/* Attach the arrow to the Scene */
		mScene.attachChild(mArrowSprite);
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// If a user moves their finger on the device
		if(pSceneTouchEvent.isActionMove()){
			
			/* Set the marble's position to that of the touch even coordinates */
			mMarbleSprite.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			
			/* Calculate the difference between the two sprites x and y coordinates */
			final float dX = mMarbleSprite.getX() - mArrowSprite.getX();
			final float dY = mMarbleSprite.getY() - mArrowSprite.getY();
			
			/* Calculate the angle of rotation in radians*/
			final float angle = (float) Math.atan2(-dY, dX);
			
			/* Convert the angle from radians to degrees, adding the default image rotation */
			final float rotation = MathUtils.radToDeg(angle) + DEFAULT_IMAGE_ROTATION;
			
			/* Set the arrow's new rotation */
			mArrowSprite.setRotation(rotation);
			
			return true;
		}
		
		return false;
	}
}