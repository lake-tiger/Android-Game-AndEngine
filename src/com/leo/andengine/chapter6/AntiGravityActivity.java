package com.leo.andengine.chapter6;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class AntiGravityActivity extends BaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

	// ====================================================
	// CONSTANTS
	// ====================================================
	public static int cameraWidth = 800;
	public static int cameraHeight = 480;

	// ====================================================
	// VARIABLES
	// ====================================================
	public Scene mScene;
	public FixedStepPhysicsWorld mPhysicsWorld;
	public Body groundWallBody;
	public Body roofWallBody;
	public Body leftWallBody;
	public Body rightWallBody;

	Body gravityBody;
	Body antigravityBody;
	final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(2f, 0.5f, 0.9f);


	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new FixedStepEngine(pEngineOptions, 60);
	}

	// ====================================================
	// CREATE ENGINE OPTIONS
	// ====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), new Camera(0, 0, cameraWidth, cameraHeight));
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}

	// ====================================================
	// CREATE RESOURCES
	// ====================================================
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// Setup the ResourceManager.
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ====================================================
	// CREATE SCENE
	// ====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();
		mScene.setBackground(new Background(0.9f,0.9f,0.9f));
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	// ====================================================
	// POPULATE SCENE
	// ====================================================

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0f,-SensorManager.GRAVITY_EARTH*2), false, 8, 3);
		mScene.registerUpdateHandler(mPhysicsWorld);
		final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		final Rectangle ground = new Rectangle(cameraWidth / 2f, 6f, cameraWidth - 4f, 8f, this.getVertexBufferObjectManager());
		final Rectangle roof = new Rectangle(cameraWidth / 2f, cameraHeight - 6f, cameraWidth - 4f, 8f, this.getVertexBufferObjectManager());
		final Rectangle left = new Rectangle(6f, cameraHeight / 2f, 8f, cameraHeight - 4f, this.getVertexBufferObjectManager());
		final Rectangle right = new Rectangle(cameraWidth - 6f, cameraHeight / 2f, 8f, cameraHeight - 4f, this.getVertexBufferObjectManager());
		ground.setColor(0f, 0f, 0f);
		roof.setColor(0f, 0f, 0f);
		left.setColor(0f, 0f, 0f);
		right.setColor(0f, 0f, 0f);
		groundWallBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, WALL_FIXTURE_DEF);
		roofWallBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, WALL_FIXTURE_DEF);
		leftWallBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF);
		rightWallBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF);
		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);


		Rectangle GravityRect = new Rectangle(300f,240f,100f,100f,this.getEngine().getVertexBufferObjectManager());
		GravityRect.setColor(0f, 0.7f, 0f);
		mScene.attachChild(GravityRect);
		mScene.registerTouchArea(GravityRect);
		gravityBody = PhysicsFactory.createBoxBody(mPhysicsWorld, GravityRect, BodyType.DynamicBody, boxFixtureDef);
		gravityBody.setLinearDamping(0.4f);
		gravityBody.setAngularDamping(0.6f);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(GravityRect, gravityBody));

		Rectangle AntiGravityRect = new Rectangle(500f,240f,100f,100f,this.getEngine().getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(final float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				antigravityBody.applyForce(
						-mPhysicsWorld.getGravity().x * antigravityBody.getMass(),
						-mPhysicsWorld.getGravity().y * antigravityBody.getMass(),
						antigravityBody.getWorldCenter().x, antigravityBody.getWorldCenter().y);
			}
		};
		AntiGravityRect.setColor(0f, 0f, 0.7f);
		mScene.attachChild(AntiGravityRect);
		mScene.registerTouchArea(AntiGravityRect);
		antigravityBody = PhysicsFactory.createBoxBody(mPhysicsWorld, AntiGravityRect, BodyType.DynamicBody, boxFixtureDef);
		antigravityBody.setLinearDamping(0.4f);
		antigravityBody.setAngularDamping(0.6f);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(AntiGravityRect, antigravityBody));


		mScene.setOnSceneTouchListener(this);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return true;
	}

	@Override
	public void onAccelerationAccuracyChanged(
			AccelerationData pAccelerationData) {}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		this.disableAccelerationSensor();
	}
}