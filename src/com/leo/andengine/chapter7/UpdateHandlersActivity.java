package com.leo.andengine.chapter7;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.graphics.Typeface;

public class UpdateHandlersActivity extends BaseGameActivity {

	// ====================================================
	// CONSTANTS
	// ====================================================
	public static int cameraWidth = 800;
	public static int cameraHeight = 480;

	// ====================================================
	// VARIABLES
	// ====================================================
	public Scene mScene;
	public Font fontDefault32Bold;
	public Text countingText;
	public int countingInt = 0;

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
		fontDefault32Bold = FontFactory.create(mEngine.getFontManager(), mEngine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  32f, true, Color.BLACK_ARGB_PACKED_INT);
		fontDefault32Bold.load();
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
		countingText = new Text(400f, 240f, fontDefault32Bold, "0", 10, this.getVertexBufferObjectManager());
		mScene.attachChild(countingText);
		mScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				countingInt++;
				countingText.setText(String.valueOf(countingInt));
			}
			@Override public void reset() {}
		});
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}