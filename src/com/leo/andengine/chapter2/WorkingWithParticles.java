package com.leo.andengine.chapter2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.UncoloredSprite;
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

public class WorkingWithParticles extends BaseGameActivity {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private Scene mScene;
	private Camera mCamera;

	// Particle texture region
	private ITextureRegion mTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		BuildableBitmapTextureAtlas bitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 40, 40, TextureOptions.BILINEAR);

		mTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, getAssets(), "marble.png");

		try {
			bitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		bitmapTextureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		/* Define the center point of the particle system spawn location */
		final int particleSpawnCenterX = (int) (WIDTH * 0.5f);
		final int particleSpawnCenterY = (int) (HEIGHT * 0.5f);
		
		/* Create the particle emitter */
		PointParticleEmitter particleEmitter = new PointParticleEmitter(particleSpawnCenterX, particleSpawnCenterY);

		/* Define the particle system properties */
		final float minSpawnRate = 25;
		final float maxSpawnRate = 50;
		final int maxParticleCount = 150;
		
		/* Create the particle system */
		BatchedSpriteParticleSystem particleSystem = new BatchedSpriteParticleSystem(
				particleEmitter, minSpawnRate, maxSpawnRate, maxParticleCount,
				mTextureRegion,
				mEngine.getVertexBufferObjectManager());

		/* Add an acceleration initializer to the particle system */
		particleSystem.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(25f, -25f, 50f, 100f));
		
		/* Add an expire initializer to the particle system */
		particleSystem.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(4));
		
		/* Add a particle modifier to the particle system */
		particleSystem.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0f, 3f, 0.2f, 1f));
		
		/* Attach the particle system to the Scene */
		mScene.attachChild(particleSystem);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}