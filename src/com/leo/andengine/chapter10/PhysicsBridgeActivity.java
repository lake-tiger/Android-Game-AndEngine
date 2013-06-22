package com.leo.andengine.chapter10;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class PhysicsBridgeActivity extends BaseGameActivity {

	// ====================================================
	// CONSTANTS
	// ====================================================
	
	// ====================================================
	// VARIABLES
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
		final FixedStepPhysicsWorld mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0,-SensorManager.GRAVITY_EARTH), false, 8, 3);
		pScene.registerUpdateHandler(mPhysicsWorld);

		FixtureDef groundFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f, 0f);
		Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, 0f, 0f, 0f, 0f, BodyType.StaticBody, groundFixtureDef);

		createBridge(groundBody, new float[] {0f,240f}, 800f, 16, 40f, 10f, 4f, 0.1f, 0.5f, pScene, mPhysicsWorld, this.getVertexBufferObjectManager());

		Rectangle boxRect = new Rectangle(100f,400f,50f,50f,this.getVertexBufferObjectManager());
		FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(25f, 0.5f, 0.5f);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boxRect, PhysicsFactory.createBoxBody(mPhysicsWorld, boxRect, BodyType.DynamicBody, boxFixtureDef)));
		pScene.attachChild(boxRect);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public void createBridge(Body pGroundBody, final float[] pLeftHingeAnchorPoint, final float pRightHingeAnchorPointX, 
			final int pNumSegments, final float pSegmentsWidth, final float pSegmentsHeight,
			final float pSegmentDensity, final float pSegmentElasticity, final float pSegmentFriction, 
			IEntity pScene, PhysicsWorld pPhysicsWorld, VertexBufferObjectManager pVertexBufferObjectManager) {
		
		final Rectangle[] BridgeSegments = new Rectangle[pNumSegments];
		final Body[] BridgeSegmentsBodies = new Body[pNumSegments];
		final FixtureDef BridgeSegmentFixtureDef = PhysicsFactory.createFixtureDef(pSegmentDensity, pSegmentElasticity, pSegmentFriction);
		final float BridgeWidthConstant = pRightHingeAnchorPointX - pLeftHingeAnchorPoint[0] + pSegmentsWidth;
		final float BridgeSegmentSpacing = (BridgeWidthConstant / (pNumSegments+1) - pSegmentsWidth/2f);
		
		for(int i = 0; i < pNumSegments; i++) {
			BridgeSegments[i] = new Rectangle(((BridgeWidthConstant / (pNumSegments+1))*i) + pLeftHingeAnchorPoint[0] + BridgeSegmentSpacing, pLeftHingeAnchorPoint[1]-pSegmentsHeight/2f, pSegmentsWidth, pSegmentsHeight, pVertexBufferObjectManager);
			BridgeSegments[i].setColor(0.97f, 0.75f, 0.54f);
			pScene.attachChild(BridgeSegments[i]);

			BridgeSegmentsBodies[i] = PhysicsFactory.createBoxBody(pPhysicsWorld, BridgeSegments[i], BodyType.DynamicBody, BridgeSegmentFixtureDef);
			BridgeSegmentsBodies[i].setLinearDamping(1f);
			pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(BridgeSegments[i], BridgeSegmentsBodies[i]));

			final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
			if(i==0) {
				Vector2 anchorPoint = new Vector2(BridgeSegmentsBodies[i].getWorldCenter().x - (BridgeSegmentSpacing/2 + pSegmentsWidth/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BridgeSegmentsBodies[i].getWorldCenter().y);
				revoluteJointDef.initialize(pGroundBody, BridgeSegmentsBodies[i], anchorPoint);

			} else {
				Vector2 anchorPoint = new Vector2((BridgeSegmentsBodies[i].getWorldCenter().x + BridgeSegmentsBodies[i-1].getWorldCenter().x)/2, BridgeSegmentsBodies[i].getWorldCenter().y);
				revoluteJointDef.initialize(BridgeSegmentsBodies[i-1], BridgeSegmentsBodies[i], anchorPoint);
			}
			pPhysicsWorld.createJoint(revoluteJointDef);
			if(i==pNumSegments-1) {
				Vector2 anchorPoint = new Vector2(BridgeSegmentsBodies[i].getWorldCenter().x + (BridgeSegmentSpacing/2 + pSegmentsWidth/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BridgeSegmentsBodies[i].getWorldCenter().y);
				revoluteJointDef.initialize(pGroundBody, BridgeSegmentsBodies[i], anchorPoint);
				pPhysicsWorld.createJoint(revoluteJointDef);
			}
		}
	}
	
}