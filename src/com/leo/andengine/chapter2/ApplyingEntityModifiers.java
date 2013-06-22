//package com.leo.andengine.chapter2;
//
//import org.andengine.engine.camera.Camera;
//import org.andengine.engine.options.EngineOptions;
//import org.andengine.engine.options.ScreenOrientation;
//import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
//import org.andengine.entity.modifier.CardinalSplineMoveModifier;
//import org.andengine.entity.modifier.CardinalSplineMoveModifier.CardinalSplineMoveModifierConfig;
//import org.andengine.entity.modifier.DelayModifier;
//import org.andengine.entity.modifier.FadeInModifier;
//import org.andengine.entity.modifier.FadeOutModifier;
//import org.andengine.entity.modifier.JumpModifier;
//import org.andengine.entity.modifier.LoopEntityModifier;
//import org.andengine.entity.modifier.MoveModifier;
//import org.andengine.entity.modifier.MoveYModifier;
//import org.andengine.entity.modifier.ParallelEntityModifier;
//import org.andengine.entity.modifier.QuadraticBezierCurveMoveModifier;
//import org.andengine.entity.modifier.RotationModifier;
//import org.andengine.entity.modifier.ScaleModifier;
//import org.andengine.entity.modifier.SequenceEntityModifier;
//import org.andengine.entity.primitive.Rectangle;
//import org.andengine.entity.scene.Scene;
//import org.andengine.ui.activity.BaseGameActivity;
//import org.andengine.util.modifier.ease.EaseBounceOut;
//import org.andengine.util.modifier.ease.EaseQuartIn;
//
//public class ApplyingEntityModifiers extends BaseGameActivity {
//
//	public static final int WIDTH = 800;
//	public static final int HEIGHT = 480;
//
//	private final int RECTANGLE_DIMENSIONS = 80;
//
//	private Scene mScene;
//	private Camera mCamera;
//
//	private Rectangle mRectangle;
//
//	@Override
//	public EngineOptions onCreateEngineOptions() {
//		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
//
//		EngineOptions engineOptions = new EngineOptions(true,
//				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
//				mCamera);
//
//		return engineOptions;
//	}
//
//	@Override
//	public void onCreateResources(
//			OnCreateResourcesCallback pOnCreateResourcesCallback) {
//
//		pOnCreateResourcesCallback.onCreateResourcesFinished();
//	}
//
//	@Override
//	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
//		mScene = new Scene();
//
//		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
//	}
//
//	@Override
//	public void onPopulateScene(Scene pScene,
//			OnPopulateSceneCallback pOnPopulateSceneCallback) {
//
//		/* Define the rectangle's width/height values */
//		final int rectangleDimension = 80;
//		
//		/* Define the initial rectangle position in the bottom 
//		 * left corner of the Scene */
//		final int initialPosition = (int) (rectangleDimension * 0.5f);
//		
//		/* Create the Entity which we will apply modifiers to */
//		Rectangle rectangle = new Rectangle(initialPosition, initialPosition, rectangleDimension, rectangleDimension, mEngine.getVertexBufferObjectManager());
//		
//		/* Set the rectangle's color to white so we can see it on the Scene */
//		rectangle.setColor(org.andengine.util.adt.color.Color.WHITE);
//		
//		/* Attach the rectangle to the Scene */
//		mScene.attachChild(rectangle);
//		
//		/* Define the movement modifier values */
//		final float duration = 3;
//		final float fromX = initialPosition;
//		final float toX = WIDTH - rectangleDimension * 0.5f;
//		final float fromY = initialPosition;
//		final float toY = HEIGHT - rectangleDimension * 0.5f;
//		
//		MoveModifier moveModifier = new MoveModifier(initialPosition, initialPosition, initialPosition, initialPosition, initialPosition);
//		
//		rectangle.registerEntityModifier(pEntityModifier)
//		
//		/* Setup the rectangle initially */
//		mRectangle = new Rectangle(initialX, initialY, RECTANGLE_DIMENSIONS, RECTANGLE_DIMENSIONS, mEngine.getVertexBufferObjectManager());
//		
//		/* Set the rectangle's color to white */
//		mRectangle.setColor(org.andengine.util.adt.color.Color.WHITE);
//		
//		/* Attach the rectangle to the Scene */
//		mScene.attachChild(mRectangle);
//		
//		// Movement from top left to top right
//		ParallelEntityModifier parallelModifierA = new ParallelEntityModifier(
//				new QuadraticBezierCurveMoveModifier(4, initialX, initialY, WIDTH * 0.5f, HEIGHT * 0.5f, WIDTH - initialX, initialY),
//				new SequenceEntityModifier(
//						new RotationModifier(2, 0, -360),
//						new RotationModifier(2, -360, 720)));
//		
//		// Movement from top right to bottom right
//		ParallelEntityModifier parallelModifierB = new ParallelEntityModifier(
//				new MoveYModifier(3, initialY, HEIGHT - initialY, EaseBounceOut.getInstance()),
//				new SequenceEntityModifier(
//						new ScaleModifier(0.7f, 1, 0.3f),
//						new DelayModifier(2f),
//						new ScaleModifier(0.7f, 0.3f, 1, EaseQuartIn.getInstance())));
//		
//		// Movement from bottom right to bottom left
//		ParallelEntityModifier parallelModifierC = new ParallelEntityModifier(
//				new JumpModifier(1, WIDTH - initialX, initialX, HEIGHT - initialY, HEIGHT - initialY, 100),
//				new SequenceEntityModifier(
//						new FadeOutModifier(0.3f),
//						new FadeInModifier(0.7f)));
//		
//		// Create our spline movement config
//		CardinalSplineMoveModifierConfig config = new CardinalSplineMoveModifierConfig(4, -1);
//		
//		// Setup the x coords for spline movement
//		final float xCoords[] = 
//		{
//				HALF_RECTANGLE_DIMENSIONS, // x1
//				WIDTH * 0.5f, // x2
//				WIDTH - HALF_RECTANGLE_DIMENSIONS, // x3
//				HALF_RECTANGLE_DIMENSIONS // x4
//		};
//		
//		// Setup the y coords for spline movement
//		final float yCoords[] =
//		{
//			HEIGHT - HALF_RECTANGLE_DIMENSIONS, // y1
//			HALF_RECTANGLE_DIMENSIONS, // y2
//			HEIGHT - HALF_RECTANGLE_DIMENSIONS, // y3
//			HALF_RECTANGLE_DIMENSIONS // y4
//		};
//		
//		// Apply the x and y coords to the config
//		for(int i = 0; i < xCoords.length; i++){
//			config.setControlPoint(i, xCoords[i], yCoords[i]);
//		}
//		
//		// Setup the cardinal spline modifier,  with the above config
//		CardinalSplineMoveModifier ModifierD = new CardinalSplineMoveModifier(10, config);
//		
//		
//		// Setup our modifiers to fire in sequence
//		SequenceEntityModifier sequenceModifier = new SequenceEntityModifier(
//				parallelModifierA,
//				parallelModifierB,
//				parallelModifierC,
//				ModifierD);
//		
//		// Setup the modifier sequence to continuously loop
//		LoopEntityModifier loopModifier = new LoopEntityModifier(sequenceModifier);
//		
//		// Register the modifier loop to the rectangle
//		mRectangle.registerEntityModifier(loopModifier);
//
//		pOnPopulateSceneCallback.onPopulateSceneFinished();
//	}
//}