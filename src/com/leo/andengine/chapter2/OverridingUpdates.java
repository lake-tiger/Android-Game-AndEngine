package com.leo.andengine.chapter2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

public class OverridingUpdates extends BaseGameActivity {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	
	private static final int RECTANGLE_WIDTH = 80;
	private static final int RECTANGLE_HEIGHT = RECTANGLE_WIDTH;
	
	private Scene mScene;
	private Camera mCamera;
	
	private Rectangle mRectangleOne;
	private Rectangle mRectangleTwo;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);

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

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		/* Define the first rectangle's coordinates in the center of the Scene */
		final float x = WIDTH * 0.5f;
		final float y = HEIGHT * 0.5f;
		
		/* Create the first rectangle in the center of the Scene */
		mRectangleOne = new Rectangle(x, y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, mEngine.getVertexBufferObjectManager()){
			
			/* Value which determines the rotation speed of this Entity */
			final int rotationIncrementalFactor = 25;
			
			/* Override the onManagedUpdate() method of this Entity */
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				/* Calculate a rotation offset based on time passed */
				final float rotationOffset = pSecondsElapsed * rotationIncrementalFactor;
				
				/* Apply the rotation offset to this Entity */
				this.setRotation(this.getRotation() + rotationOffset);
				
				/* Proceed with the rest of this Entity's update process */
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		/* Set the first rectangle's color to blue */
		mRectangleOne.setColor(org.andengine.util.adt.color.Color.BLUE);
		
		/* Attach the first rectangle to the Scene */
		mScene.attachChild(mRectangleOne);
		
		/* Create the second rectangle at the bottom left corner of the Scene */
		mRectangleTwo = new Rectangle(RECTANGLE_WIDTH * 0.5f, RECTANGLE_HEIGHT * 0.5f, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, mEngine.getVertexBufferObjectManager()){
			
			/* Value to increment this rectangle's position by on each update */
			final int incrementXValue = 5;
			
			/* Obtain half the Entity's width and height values */
			final float halfWidth = this.getWidth() * 0.5f;
			final float halfHeight = this.getHeight() * 0.5f;
			
			/* Override the onManagedUpdate() method of this Entity */
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				
				/* Obtain the current x/y values */
				final float currentX = this.getX();
				final float currentY = this.getY();
				
				/* obtain the max width and next height, used for condition checking */
				final float maxWidth = currentX + halfWidth;
				final float nextHeight = currentY + halfHeight;
				
				// On every update...
				/* Increment the x position if this Entity is within the camera WIDTH */
				 if(maxWidth <= WIDTH){
					/* Increase this Entity's x value by 5 pixels */
					this.setX(currentX + incrementXValue);
				} else {
					/* Reset the Entity back to the bottom left of the Scene if it exceeds the mCamera's
					 * HEIGHT value */
					if(nextHeight >= HEIGHT){
						this.setPosition(halfWidth, halfHeight);
					} else {
						/* if this Entity reaches the WIDTH value of our camera, move it
						 * back to the left side of the Scene and slightly increment its y position */
						this.setPosition(halfWidth, nextHeight);
					}
				}
				
				/* If the two rectangle's are colliding, set this rectangle's color to GREEN */
				if(this.collidesWith(mRectangleOne) && this.getColor() != org.andengine.util.adt.color.Color.GREEN){
					this.setColor(org.andengine.util.adt.color.Color.GREEN);
					
				/* If the rectangle's are no longer colliding, set this rectangle's color to RED */
				} else if(this.getColor() != org.andengine.util.adt.color.Color.RED){
					this.setColor(org.andengine.util.adt.color.Color.RED);
				}
				
				/* Proceed with the rest of this Entity's update process */
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		/* Set the second rectangle's color to red */
		mRectangleTwo.setColor(org.andengine.util.adt.color.Color.RED);
		
		/* Attach the second rectangle to the Scene */
		mScene.attachChild(mRectangleTwo);
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}