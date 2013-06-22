package com.leo.andengine.chapter5;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.util.math.MathUtils;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameLevel extends ManagedGameScene implements IAccelerationListener, IOnSceneTouchListener {
	
	// ====================================================
	// CONSTANTS
	// ====================================================
	public static int cameraWidth = 800;
	public static int cameraHeight = 480;

	// ====================================================
	// VARIABLES
	// ====================================================
	public FixedStepPhysicsWorld mPhysicsWorld;
	
	public Body slopeRectBody;
	public Body topBalanceBarBody;
	public Body leftBalanceBarBody;
	public Body rightBalanceBarBody;
	public ArrayList<Body> ballBodies;
	public ArrayList<AnimatedSprite> ballAnimatedSprites;
	
	private float pAccelerationDataX = 0;
	private int mFaceCount = 0;
	private int ballFrequency = 480;
	
    private boolean isPotLeftPressed = false;
    private boolean isPotRightPressed = false;
    private volatile boolean isTopBalanceBarHalted = false;
    private boolean isVibratorEnabled = true;
    private boolean isVibratorStarted = false;

	// ====================================================
	// CREATE SCENE
	// ====================================================
	@Override
	public void onLoadScene() {
		super.onLoadScene();
		final float autoParallaxSpeed = 5;
        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, autoParallaxSpeed);
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(cameraWidth/2, cameraHeight/2, ResourceManager.mParallaxLayerBack, ResourceManager.getInstance().engine.getVertexBufferObjectManager())));
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(cameraWidth/2, 300, ResourceManager.mParallaxLayerMid, ResourceManager.getInstance().engine.getVertexBufferObjectManager())));
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(cameraWidth/2, ResourceManager.mParallaxLayerFront.getHeight()/2, ResourceManager.mParallaxLayerFront, ResourceManager.getInstance().engine.getVertexBufferObjectManager())));
        this.setBackground(autoParallaxBackground);
        
        this.ballAnimatedSprites = new ArrayList<AnimatedSprite>();
	    this.ballBodies = new ArrayList<Body>();
        
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0f,SensorManager.GRAVITY_EARTH*1.2f), false, 8, 3);
		this.registerUpdateHandler(mPhysicsWorld);
		
		final FixtureDef balanceBarFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f, 0.5f);
		
		final float slopeRectWidth = 300f;
		final float slopeRectHeight = 8f;
		final float slopeRectX = 0f;
		final float slopeRectY = 40f;
		
		final Rectangle slopeBalanceBarRect = new Rectangle(slopeRectX,slopeRectY, slopeRectWidth, slopeRectHeight,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
//		slopeBalanceBarRect.setRotation(15f);
		slopeBalanceBarRect.setColor(1f, 1f, 1f);
        this.attachChild(slopeBalanceBarRect);
        slopeRectBody = PhysicsFactory.createBoxBody(mPhysicsWorld, slopeBalanceBarRect, BodyType.StaticBody, balanceBarFixtureDef);
        slopeRectBody.setLinearDamping(1f);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(slopeBalanceBarRect, slopeRectBody, PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
//		
//		final float boxRect1Width = 300f;
//		final float boxRect1Height = 8f;
//		final float boxRect1X = (cameraWidth - boxRect1Width)/2f;
//		final float boxRect1Y = 175f;
//		
//		final Rectangle topBalanceBarRect = new Rectangle(boxRect1X,boxRect1Y,boxRect1Width,boxRect1Height,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
//		topBalanceBarRect.setColor(1f, 1f, 1f);
//		this.attachChild(topBalanceBarRect);
//		topBalanceBarBody = PhysicsFactory.createBoxBody(mPhysicsWorld, topBalanceBarRect, BodyType.KinematicBody, balanceBarFixtureDef);
//		topBalanceBarBody.setAngularDamping(1f);
//		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(topBalanceBarRect, topBalanceBarBody, PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
//		
//		final Rectangle leftBalanceBarRect = new Rectangle(topBalanceBarRect.getX()-150f,topBalanceBarRect.getY()+130f,boxRect1Width-50f,boxRect1Height,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
//		leftBalanceBarRect.setColor(1f, 1f, 1f);
//		this.attachChild(leftBalanceBarRect);
//		leftBalanceBarBody = PhysicsFactory.createBoxBody(mPhysicsWorld, leftBalanceBarRect, BodyType.KinematicBody, balanceBarFixtureDef);
//		leftBalanceBarBody.setAngularDamping(1f);
//		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(leftBalanceBarRect, leftBalanceBarBody, PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
//		
//		final Rectangle rightBalanceBarRect = new Rectangle(leftBalanceBarRect.getX()+350f,leftBalanceBarRect.getY(),boxRect1Width-50f,boxRect1Height,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
//		rightBalanceBarRect.setColor(1f, 1f, 1f);
//		this.attachChild(rightBalanceBarRect);
//		rightBalanceBarBody = PhysicsFactory.createBoxBody(mPhysicsWorld, rightBalanceBarRect, BodyType.KinematicBody, balanceBarFixtureDef);
//		rightBalanceBarBody.setAngularDamping(1f);
//		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(rightBalanceBarRect, rightBalanceBarBody, PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
//		
//		
//		/* define the three receiver you can find below. */
//		
//		final float potWidth = 160f;
//        final float potHeight = 60f;
//        
//        Rectangle potLeftRect = new Rectangle(0f, cameraHeight-potHeight, potWidth, potHeight, ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
//            @Override
//            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
//                    float pTouchAreaLocalX, float pTouchAreaLocalY)
//            {
//                switch(pSceneTouchEvent.getAction()) {
//                    case TouchEvent.ACTION_DOWN:
//                        isPotLeftPressed = true;
//                        break;
//                    case TouchEvent.ACTION_UP:
//                        isPotLeftPressed = false;
//                        break;
//                }
//                return true;
//            }
//        }; 
//        potLeftRect.setColor(1f, 170/255f, 170/255f);
//        ResourceManager.ballPotHUD.registerTouchArea(potLeftRect);
//        ResourceManager.ballPotHUD.attachChild(potLeftRect);
//        
//        Rectangle potRightRect = new Rectangle(cameraWidth-potWidth, cameraHeight-potHeight, potWidth, potHeight, ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
//            @Override
//            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
//                    float pTouchAreaLocalX, float pTouchAreaLocalY)
//            {
//                switch(pSceneTouchEvent.getAction()) {
//                    case TouchEvent.ACTION_DOWN:
//                        isPotRightPressed = true;
//                        break;
//                    case TouchEvent.ACTION_UP:
//                        isPotRightPressed = false;
//                        break;
//                }
//                return true;
//            }
//        }; 
//        potRightRect.setColor(1f, 234/255f, 1/255f);
//        ResourceManager.ballPotHUD.registerTouchArea(potRightRect);
//        ResourceManager.ballPotHUD.attachChild(potRightRect);
//		
//        Rectangle potMiddleRect = new Rectangle((cameraWidth-potWidth)/2, cameraHeight-potHeight, potWidth, potHeight, ResourceManager.getInstance().engine.getVertexBufferObjectManager()); 
//        potMiddleRect.setColor(0f, 254/255f, 233/255f);
//        ResourceManager.ballPotHUD.registerTouchArea(potMiddleRect);
//        ResourceManager.ballPotHUD.attachChild(potMiddleRect);
//        
//        mPhysicsWorld.setContactListener(new ContactListener(){
//
//            @Override
//            public void beginContact(Contact contact) {
//                if(contact.isTouching()) {
////	                    if(areBodiesContacted(staticBody,dynamicBody,contact)) {}
//                }
//                for(int i=0; i<ballBodies.size(); i++) {
//                    if(isBodyContacted(ballBodies.get(i),contact)) {
//                        isVibratorStarted = true;
//                    }
//                }
//            }
//
//            @Override
//            public void endContact(Contact contact) {
////	                if(areBodiesContacted(staticBody,dynamicBody,contact)){}
////	                if(isBodyContacted(dynamicBody,contact)) {}
//            }
//
//            @Override
//            public void preSolve(Contact contact, Manifold oldManifold) {
////	                if(areBodiesContacted(dynamicBody, staticBody, contact))
////	                    if(dynamicBody.getWorldCenter().y < staticBody.getWorldCenter().y)
////	                        contact.setEnabled(false);
//            }
//            @Override
//            public void postSolve(Contact contact, ContactImpulse impulse) {
////	                if(areBodiesContacted(dynamicBody, staticBody, contact)) {
////	                    maxImpulse = impulse.getNormalImpulses()[0];
////	                    for(int i = 1; i < impulse.getNormalImpulses().length; i++)
////	                        maxImpulse = Math.max(impulse.getNormalImpulses()[i], maxImpulse);
////	                    if(maxImpulse>400f)
////	                        dynamicBody.setAngularVelocity(30f);
////	                }
//            }
//        });
//        
//		this.registerUpdateHandler(new IUpdateHandler() {
//            @Override
//            public void onUpdate(float pSecondsElapsed) {
//                
//                /* left & right buttons pressed */
//                if(isPotLeftPressed && isPotRightPressed) {
//                    leftBalanceBarBody.setAngularVelocity(0f);
//                    rightBalanceBarBody.setAngularVelocity(0f);
//                }else if(isPotLeftPressed && !isPotRightPressed) {  /* left button pressed */
//                    leftBalanceBarBody.setAngularVelocity(-0.8f);
//                    rightBalanceBarBody.setAngularVelocity(-0.8f);
//                }else if(!isPotLeftPressed && isPotRightPressed) { /* right button pressed */
//                    leftBalanceBarBody.setAngularVelocity(0.8f);
//                    rightBalanceBarBody.setAngularVelocity(0.8f);
//                }
//                
//                float angle = MathUtils.radToDeg(topBalanceBarBody.getAngle());
//                if(angle > 15f && pAccelerationDataX > 0) {
//                    topBalanceBarBody.setAngularVelocity(0f);
//                    isTopBalanceBarHalted = true;
//                }else if(angle < -15f && pAccelerationDataX < 0) {
//                    topBalanceBarBody.setAngularVelocity(0f);
//                    isTopBalanceBarHalted = true;
//                }else {
//                    isTopBalanceBarHalted = false;
//                }
//                
//                float angle2 = MathUtils.radToDeg(leftBalanceBarBody.getAngle());
//                if(angle2 > 15f && !isPotLeftPressed) {
//                    leftBalanceBarBody.setAngularVelocity(0f);
//                }else if(angle2 < -15f && !isPotRightPressed) {
//                    leftBalanceBarBody.setAngularVelocity(0f);
//                }
//                
//                float angle3 = MathUtils.radToDeg(rightBalanceBarBody.getAngle());
//                if(angle3 > 15f && !isPotLeftPressed) {
//                    rightBalanceBarBody.setAngularVelocity(0f);
//                }else if(angle3 < -15f && !isPotRightPressed) {
//                    rightBalanceBarBody.setAngularVelocity(0f);
//                }
//
//                if(isVibratorStarted) {
////                    vibrate(100);
//                    isVibratorStarted = false;
//                }
//                
//                if(ballAnimatedSprites.size() > 0) {
//                    if(ballAnimatedSprites.get(0).getY() > cameraHeight + ballFrequency) {
//                        removeFace(ballAnimatedSprites.get(0));
//                        addFace();
//                        ballFrequency -= 20;
//                    }
//                }
//                
//                
//            }
//            @Override public void reset() {}
//        });
//		
//		this.registerUpdateHandler(new TimerHandler(2, new ITimerCallback() {
//		    @Override
//	        public void onTimePassed(TimerHandler pTimerHandler) {
//	            // When 4 seconds is up, switch to our menu scene
//		            addFace();
//	        }
//		}));
//		
//		this.attachChild(ResourceManager.ballPotHUD);
//		this.setOnSceneTouchListener(this);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return true;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	
	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
	    
//	    this.pAccelerationDataX = pAccelerationData.getX();
//	    if(!isTopBalanceBarHalted)
//	    this.topBalanceBarBody.setAngularVelocity(pAccelerationData.getX()/5);
	    
	}

	private void addFace() {
	    final float pX = 10f;
	    final float pY = -30f;
	    this.mFaceCount++;
	    
        final AnimatedSprite face;
        final Body body;

        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(10f, 0f, 0.5f);

        if(this.mFaceCount % 3 == 1) {
            face = new AnimatedSprite(pX, pY, ResourceManager.faceRedTextureRegion, ResourceManager.getInstance().engine.getVertexBufferObjectManager());
            face.setScale(1.5f, 1.5f);
            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
        } else if(this.mFaceCount % 3 == 2){
            face = new AnimatedSprite(pX, pY, ResourceManager.faceYellowTextureRegion, ResourceManager.getInstance().engine.getVertexBufferObjectManager());
            face.setScale(1.5f, 1.5f);
            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
        } else {
            face = new AnimatedSprite(pX, pY, ResourceManager.faceBlueTextureRegion, ResourceManager.getInstance().engine.getVertexBufferObjectManager());
            face.setScale(1.5f, 1.5f);
            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
        }
        face.animate(200, true);
        body.setAngularDamping(0.6f);
        body.setLinearDamping(1f);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
//	        this.registerTouchArea(face);
//	        this.attachChild(face);
        this.ballBodies.add(body);
        this.ballAnimatedSprites.add(face);
        this.attachChild(face);
        
    }
	
    private void removeFace(final AnimatedSprite face) {
        final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);

        this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
        this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());

//	        this.unregisterTouchArea(face);
        this.detachChild(face);
        if(this.ballAnimatedSprites.size() > 0)
        this.ballAnimatedSprites.remove(0);
        if(this.ballBodies.size() > 0)
        this.ballBodies.remove(0);
        
        System.gc();
    }
    
    public boolean isBodyContacted(Body pBody, Contact pContact)
    {
        if(pContact.getFixtureA().getBody().equals(pBody) ||
                pContact.getFixtureB().getBody().equals(pBody))
            return true;
        return false;
    }

    public boolean areBodiesContacted(Body pBody1, Body pBody2, Contact pContact)
    {
        if(pContact.getFixtureA().getBody().equals(pBody1) ||
                pContact.getFixtureB().getBody().equals(pBody1))
            if(pContact.getFixtureA().getBody().equals(pBody2) ||
                    pContact.getFixtureB().getBody().equals(pBody2))
                return true;
        return false;
    }
    
//    public void vibrate(long pMilliseconds) {
//        if(isVibratorEnabled)
//        this.mEngine.vibrate(pMilliseconds);
//    }

}