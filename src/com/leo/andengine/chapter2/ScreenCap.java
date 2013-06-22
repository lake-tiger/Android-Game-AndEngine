package com.leo.andengine.chapter2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.entity.util.ScreenCapture.IScreenCaptureCallback;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ScreenCap extends BaseGameActivity {

	public static final String TAG = "ScreenCap";

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private Scene mScene;
	private Camera mCamera;

	/* The maximum width and height of the screen to capture */
	private int mDisplayWidth;
	private int mDisplayHeight;

	/* Create a new ScreenCapture object */
	ScreenCapture mScreenCapture = new ScreenCapture();

	@SuppressLint("NewApi")
    @Override
	public EngineOptions onCreateEngineOptions() {

		/*
		 * Obtain the window manager and default display, which we will use to
		 * find out the size of the device's display (in pixels)
		 */
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();

		/* Obtain the API level of the device running the game */
		int api = android.os.Build.VERSION.SDK_INT;

		/*
		 * We're dealing with deprecated methods, so we filter older devices
		 * (less than api 13) to use the older methods, while the new API levels
		 * will use the non-deprecated methods in ord to obtain the device's
		 * display size
		 */
		if (api >= 13) {
			Point point = new Point();

			/* Pass the display size to the point object */
			display.getSize(point);

			/*
			 * pass the device's display size to our width/height variables to
			 * capture
			 */
			mDisplayWidth = point.x;
			mDisplayHeight = point.y;
		} else {

			/*
			 * If API level is less than 13, revert to using the deprecated
			 * methods used for grabbing the device's display size
			 */
			mDisplayWidth = display.getWidth();
			mDisplayHeight = display.getHeight();
		}

		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();

		/* Create a rectangle that we will take a picture of */
		Rectangle rectangle = new Rectangle(WIDTH * 0.5f, HEIGHT * 0.5f, 200,
				200, mEngine.getVertexBufferObjectManager());

		/* Set the rectangle to the color red */
		rectangle.setColor(org.andengine.util.adt.color.Color.RED);

		/* Attach the rectangle to the Scene */
		mScene.attachChild(rectangle);

		/*
		 * The mScreenCapture object IS an Entity object. We must attach it to
		 * the Scene in order to take a screen shot
		 */
		mScene.attachChild(mScreenCapture);

		/* Create a directory for our game if one does not already exist */
		FileUtils.ensureDirectoriesExistOnExternalStorage(this, "");

		/* Capture the entire screen screen */
		mScreenCapture.capture(mDisplayWidth, mDisplayHeight,
				FileUtils.getAbsolutePathOnExternalStorage(this, "name"
						+ ".png"), new IScreenCaptureCallback() {

					/*
					 * This method is called if the screen capture was
					 * successful
					 */
					@Override
					public void onScreenCaptured(String pFilePath) {

						/*
						 * Display that the capture was successful in LogCat and
						 * print the path to the file
						 */
						Log.i(TAG, "Successfully saved to: " + pFilePath);
					}

					/* This method is called if the screen capture failed */
					@Override
					public void onScreenCaptureFailed(String pFilePath,
							Exception pException) {

						/*
						 * Display that the capture has failed in LogCat along
						 * with the exception thrown
						 */
						Log.e(TAG,
								pFilePath + " : "
										+ pException.getLocalizedMessage());
					}
				});

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}