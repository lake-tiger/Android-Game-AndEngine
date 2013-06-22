package com.leo.andengine.chapter10;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class MovingPhysicsPlatformActivity extends BaseGameActivity {

	// ====================================================
	// CONSTANTS
	// ====================================================

	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new FixedStepEngine(pEngineOptions, 60);
	}

	// ====================================================
	// CREATE ENGINE OPTIONS
	// ====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), new Camera(0, 0, 800, 480)).setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	}

	// ====================================================
	// CREATE RESOURCES
	// ====================================================
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ====================================================
	// CREATE SCENE
	// ====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		Scene mScene = new Scene();
		mScene.setBackground(new Background(0.9f,0.9f,0.9f));
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	// ====================================================
	// POPULATE SCENE
	// ====================================================

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {

		FixedStepPhysicsWorld mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0,-SensorManager.GRAVITY_EARTH*2f), false, 8, 3); 
		pScene.registerUpdateHandler(mPhysicsWorld);

		Rectangle platformRect = new Rectangle(400f,200f,250f,20f,this.getVertexBufferObjectManager());
		platformRect.setColor(0f, 0f, 0f);
		final FixtureDef platformFixtureDef = PhysicsFactory.createFixtureDef(20f, 0f, 1f);
		final Body platformBody = PhysicsFactory.createBoxBody(mPhysicsWorld, platformRect, BodyType.KinematicBody, platformFixtureDef);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platformRect, platformBody));
		pScene.attachChild(platformRect);

		float platformRelativeMinX = -200f;
		float platformRelativeMaxX = 200f;
		final float platformVelocity = 3f;
		final float platformMinXWorldCoords = (platformRect.getX() + platformRelativeMinX) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float platformMaxXWorldCoords = (platformRect.getX() + platformRelativeMaxX) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

		platformBody.setLinearVelocity(platformVelocity, 0f);

		pScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(platformBody.getWorldCenter().x > platformMaxXWorldCoords) {
					platformBody.setTransform(platformMaxXWorldCoords, platformBody.getWorldCenter().y, platformBody.getAngle());
					platformBody.setLinearVelocity(-platformVelocity, 0f);
				} else if(platformBody.getWorldCenter().x < platformMinXWorldCoords) {
					platformBody.setTransform(platformMinXWorldCoords, platformBody.getWorldCenter().y, platformBody.getAngle());
					platformBody.setLinearVelocity(platformVelocity, 0f);
				}
			}
			@Override
			public void reset() {}
		});


		Rectangle boxRect = new Rectangle(400f,240f,60f,60f,this.getVertexBufferObjectManager());
		boxRect.setColor(0.2f, 0.2f, 0.2f);
		FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(200f, 0f, 1f);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boxRect, PhysicsFactory.createBoxBody(mPhysicsWorld, boxRect, BodyType.DynamicBody, boxFixtureDef)));
		pScene.attachChild(boxRect);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}