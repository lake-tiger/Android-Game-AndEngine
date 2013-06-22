package com.leo.andengine.chapter6;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.extension.physics.box2d.util.triangulation.EarClippingTriangulator;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.list.ListUtils;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class UniqueBodiesActivity extends BaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

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

	@SuppressWarnings("unchecked")
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


		List<Vector2> UniqueBodyVertices = new ArrayList<Vector2>();
		UniqueBodyVertices.addAll((List<Vector2>) ListUtils.toList(new Vector2[] {
//				new Vector2(0f,0f),
				new Vector2(-53f,-75f),
				new Vector2(-107f,-14f),
				new Vector2(-101f,41f),
				new Vector2(-71f,74f),
				new Vector2(69f,74f),
				new Vector2(98f,41f),
				new Vector2(104f,-14f),
				new Vector2(51f,-75f),
				new Vector2(79f,9f),
				new Vector2(43f,34f),
				new Vector2(-46f,34f),
				new Vector2(-80f,9f)
		}));

		List<Vector2> UniqueBodyVerticesTriangulated = new EarClippingTriangulator().computeTriangles(UniqueBodyVertices);

		float[] MeshTriangles = new float[UniqueBodyVerticesTriangulated.size() * 3];
		for(int i = 0; i < UniqueBodyVerticesTriangulated.size(); i++){
			MeshTriangles[i*3] = UniqueBodyVerticesTriangulated.get(i).x;
			MeshTriangles[i*3+1] = UniqueBodyVerticesTriangulated.get(i).y;
			UniqueBodyVerticesTriangulated.get(i).mul(1/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		}
		Mesh UniqueBodyMesh = new Mesh(400f, 260f, MeshTriangles, UniqueBodyVerticesTriangulated.size(), DrawMode.TRIANGLES, this.getVertexBufferObjectManager());
		UniqueBodyMesh.setColor(1f, 0f, 0f);
		mScene.attachChild(UniqueBodyMesh);

		FixtureDef uniqueBodyFixtureDef = PhysicsFactory.createFixtureDef(20f, 0.5f, 0.5f);
		Body uniqueBody = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMesh, UniqueBodyVerticesTriangulated, BodyType.DynamicBody, uniqueBodyFixtureDef);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(UniqueBodyMesh, uniqueBody));

		FixtureDef BoxBodyFixtureDef = PhysicsFactory.createFixtureDef(20f, 0.6f, 0.5f);
		Rectangle Box1 = new Rectangle(0f,0f,20f,20f,this.getVertexBufferObjectManager());
		mScene.attachChild(Box1);
		PhysicsFactory.createBoxBody(mPhysicsWorld, Box1, BodyType.StaticBody, BoxBodyFixtureDef);

		Rectangle Box2 = new Rectangle(600f,160f,20f,20f,this.getVertexBufferObjectManager());
		mScene.attachChild(Box2);
		PhysicsFactory.createBoxBody(mPhysicsWorld, Box2, BodyType.StaticBody, BoxBodyFixtureDef);


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