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
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MultipleFixtureBodiesActivity extends BaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

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



		Rectangle nonbouncyBoxRect = new Rectangle(0f,0f,100f,100f,this.getEngine().getVertexBufferObjectManager());
		nonbouncyBoxRect.setColor(0f, 0f, 0f);
		nonbouncyBoxRect.setAnchorCenter(
				((nonbouncyBoxRect.getWidth() / 2) - nonbouncyBoxRect.getX()) / nonbouncyBoxRect.getWidth(),
				((nonbouncyBoxRect.getHeight() / 2) - nonbouncyBoxRect.getY()) / nonbouncyBoxRect.getHeight());
		mScene.attachChild(nonbouncyBoxRect);

		Rectangle bouncyBoxRect = new Rectangle(0f,-55f,90f,10f,this.getEngine().getVertexBufferObjectManager());
		bouncyBoxRect.setColor(0f, 0.75f, 0f);
		bouncyBoxRect.setAnchorCenter(
				((bouncyBoxRect.getWidth() / 2) - bouncyBoxRect.getX()) / bouncyBoxRect.getWidth(),
				((bouncyBoxRect.getHeight() / 2) - bouncyBoxRect.getY()) / bouncyBoxRect.getHeight());
		mScene.attachChild(bouncyBoxRect);

		Body multiFixtureBody = mPhysicsWorld.createBody(new BodyDef());
		multiFixtureBody.setType(BodyType.DynamicBody);

		FixtureDef nonbouncyBoxFixtureDef = PhysicsFactory.createFixtureDef(20, 0.0f, 0.5f);
		final PolygonShape nonbouncyBoxShape = new PolygonShape();
		nonbouncyBoxShape.setAsBox(
				(nonbouncyBoxRect.getWidth() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
				(nonbouncyBoxRect.getHeight() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
				new Vector2(
						nonbouncyBoxRect.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
						nonbouncyBoxRect.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
						0f);
		nonbouncyBoxFixtureDef.shape = nonbouncyBoxShape;
		multiFixtureBody.createFixture(nonbouncyBoxFixtureDef);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(nonbouncyBoxRect, multiFixtureBody));

		FixtureDef bouncyBoxFixtureDef = PhysicsFactory.createFixtureDef(20, 1f, 0.5f);
		final PolygonShape bouncyBoxShape = new PolygonShape();
		bouncyBoxShape.setAsBox(
				(bouncyBoxRect.getWidth() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
				(bouncyBoxRect.getHeight() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
				new Vector2(
						bouncyBoxRect.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
						bouncyBoxRect.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
						0f);
		bouncyBoxFixtureDef.shape = bouncyBoxShape;
		multiFixtureBody.createFixture(bouncyBoxFixtureDef);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bouncyBoxRect, multiFixtureBody));

		multiFixtureBody.setTransform(400f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 240f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0f);


		mScene.setOnSceneTouchListener(this);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return true;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

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