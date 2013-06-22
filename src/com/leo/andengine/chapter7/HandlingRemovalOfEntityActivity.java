package com.leo.andengine.chapter7;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.widget.Toast;

public class HandlingRemovalOfEntityActivity extends BaseGameActivity {

	// ====================================================
	// CONSTANTS
	// ====================================================
	public static int cameraWidth = 800;
	public static int cameraHeight = 480;

	// ====================================================
	// VARIABLES
	// ====================================================
	public Scene mScene;
	public Rectangle spinningRect;
	public float totalElapsedSeconds = 0f;

	// ====================================================
	// CREATE ENGINE OPTIONS
	// ====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), new Camera(0, 0, cameraWidth, cameraHeight)).setWakeLockOptions(WakeLockOptions.SCREEN_ON);
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
		mScene = new Scene();
		mScene.setBackground(new Background(0.9f,0.9f,0.9f));
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	// ====================================================
	// POPULATE SCENE
	// ====================================================
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		spinningRect = new Rectangle(400f,240f,100f,20f,this.getVertexBufferObjectManager()) {
//            @Override
//            protected void onManagedUpdate(float pSecondsElapsed)
//            {
//                super.onManagedUpdate(pSecondsElapsed);
//                spinningRect.setRotation(spinningRect.getRotation()+0.4f);
//                totalElapsedSeconds += pSecondsElapsed;
//                if(totalElapsedSeconds > 5f) {
//                    spinningRect.detachSelf();
//                }    
//            }
		};
		spinningRect.setColor(Color.BLACK);
        spinningRect.registerUpdateHandler(new IUpdateHandler() {
        	@Override
        	public void onUpdate(float pSecondsElapsed) {
        		spinningRect.setRotation(spinningRect.getRotation()+0.4f);
        		totalElapsedSeconds += pSecondsElapsed;
        		if(totalElapsedSeconds > 5f) {
        		    runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            Toast.makeText(HandlingRemovalOfEntityActivity.this, "Hello!", Toast.LENGTH_SHORT).show();
                        }
        		    });
        			runOnUpdateThread(new Runnable() {
        				@Override
        				public void run() {
        					spinningRect.detachSelf();
        				}
        		    });
        		}
        	}
        	@Override public void reset() {}
        });
		mScene.attachChild(spinningRect);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}