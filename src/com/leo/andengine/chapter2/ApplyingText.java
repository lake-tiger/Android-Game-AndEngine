package com.leo.andengine.chapter2;

import java.util.Calendar;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.graphics.Typeface;

public class ApplyingText extends BaseGameActivity {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private static final String TIME_STRING_PREFIX = "Time: ";
	private static final String TIME_FORMAT = "00:00:00";
	
	/* Obtain the maximum number of characters that our Text 
	 * object will need to display*/
	private static final int MAX_CHARACTER_COUNT = TIME_STRING_PREFIX.length() + TIME_FORMAT.length();
	
	private Scene mScene;
	private Camera mCamera;

	private Font mFont;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {

		mFont = FontFactory.create(mEngine.getFontManager(),
				mEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32f, true,
				Color.WHITE_ABGR_PACKED_INT);
		mFont.load();

		/*
		 * Prepare the mFont object for the most common characters used. This
		 * will eliminate the need for the garbage collector to run when using a
		 * letter/number that's never been used before
		 */
		mFont.prepareLetters("Time: 1234567890".toCharArray());

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

		/*
		 * The height never changes as the time ticks, so we can set a final
		 * value
		 */
		final float timeTextHeight = HEIGHT - (mFont.getLineHeight() * 0.5f);

		/* Create the time Text object which will update itself as time passes */
		Text mTimeText = new Text(0, timeTextHeight, mFont, TIME_STRING_PREFIX
				+ TIME_FORMAT, MAX_CHARACTER_COUNT, mEngine.getVertexBufferObjectManager()) {

			/*
			 * In order to avoid unnecessary updates, we will compare the value
			 * of the current second with that of the last update to the Text
			 * entity
			 */
			int lastSecond = 0;

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				Calendar c = Calendar.getInstance();

				/*
				 * We will only obtain the second for now in order to verify
				 * that it's time to update the Text's string
				 */
				final int second = c.get(Calendar.SECOND);

				/*
				 * If the last update's second value is not equal to the
				 * current...
				 */
				if (lastSecond != second) {

					/* Obtain the new hour and minute time values */
					final int hour = c.get(Calendar.HOUR);
					final int minute = c.get(Calendar.MINUTE);

					/* also, update the latest second value */
					lastSecond = second;

					/* Build a new string with the current time */
					final String timeTextSuffix = hour + ":" + minute + ":"
							+ second;

					/* Set the Text object's string to that of the new time */
					this.setText(TIME_STRING_PREFIX + timeTextSuffix);

					/*
					 * Since the width of the Text will change with every change
					 * in second, we should realign the Text position to the
					 * edge of the screen minus half the Text's width
					 */
					this.setX(WIDTH - this.getWidth() * 0.5f);
				}

				super.onManagedUpdate(pSecondsElapsed);
			}

		};

		/* Change the color of the Text to blue */
		mTimeText.setColor(0, 0, 1);

		/* Attach the Text object to the Scene */
		mScene.attachChild(mTimeText);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}