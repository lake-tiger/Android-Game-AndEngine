package com.leo.andengine.chapter2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

public class ApplyingPrimitives extends BaseGameActivity {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private static final int POINTS_PER_TRIANGLE = 3;
	
	private static final int UNUSED = 0;
	
	/* Here we define the width and height values of the house
	 * objects. The house objects consist of a base (walls), a
	 * roof, and a door */
	private static final int BASE_WIDTH = 400;
	private static final int BASE_HEIGHT = 200;
	
	private static final int ROOF_WIDTH = 600;
	private static final int ROOF_HEIGHT = 200;
	
	private static final int DOOR_WIDTH = 50;
	private static final int DOOR_HEIGHT = 100;
	
	private Scene mScene;
	private Camera mCamera;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		// We're making this house out of primitives. no are resources necessary..
		
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

		/* Setup the raw points for the base mesh. The 
		 * base mesh will represent the walls of the house, which
		 * is made up of two triangles */
		float baseBufferData[] = {
				/* First Triangle */
				0, BASE_HEIGHT, UNUSED, /* first point */
				BASE_WIDTH, BASE_HEIGHT, UNUSED, /* second point */
				BASE_WIDTH, 0, UNUSED, 	/* third point */

				/* Second Triangle */
				BASE_WIDTH, 0, UNUSED, /* first point */
				0, 0, UNUSED, /* second point */
				0, BASE_HEIGHT, UNUSED, /* third point */
		};

		/* Create the base mesh at the bottom of the screen, supplying the baseBuffer
		 * as the points to draw */
		Mesh baseMesh = new Mesh((WIDTH * 0.5f) - (BASE_WIDTH * 0.5f), 0, baseBufferData, baseBufferData.length / POINTS_PER_TRIANGLE,
				DrawMode.TRIANGLES, mEngine.getVertexBufferObjectManager());
		
		/* Set the base mesh to a brownish color */
		baseMesh.setColor(0.45f, 0.164f, 0.3f);
		
		/* Attach the baseMesh to the scene */
		mScene.attachChild(baseMesh);

		/* Setup the raw points for the roof mesh. A roof can be
		 * represented by a single triangle shape, so we'll only
		 * need to specify 3 points to make a triangle */
		float roofBufferData[] = {
				// Triangle
				0, 0, UNUSED, /* first point */
				ROOF_WIDTH * 0.5f, ROOF_HEIGHT, UNUSED, /* second point */
				ROOF_WIDTH, 0, UNUSED, /* third point */
		};

		/* Create the roof mesh at the top of the baseMesh object, centering it
		 * above the houses base mesh. */
		Mesh roofMesh = new Mesh(WIDTH * 0.5f - ROOF_WIDTH * 0.5f, BASE_HEIGHT, roofBufferData, roofBufferData.length / POINTS_PER_TRIANGLE,
				DrawMode.TRIANGLES, mEngine.getVertexBufferObjectManager());

		/* Set the roof mesh to the color red */
		roofMesh.setColor(org.andengine.util.adt.color.Color.RED);
		
		/* Attach the roofMesh to the scene */
		mScene.attachChild(roofMesh);

		/* The doorMesh will be created in lines rather than triangles, so
		 * instead of defining points in groups of 3, we can have any number
		 * of points and it wont cause problems. For the door shape, we'll
		 * use 5 points */
		float doorBufferData[] = {
				0, DOOR_HEIGHT, UNUSED, /* first point */
				DOOR_WIDTH, DOOR_HEIGHT, UNUSED, /* second point */
				DOOR_WIDTH, 0, UNUSED, /* third point */
				0, 0, UNUSED, /* fourth point */
				0, DOOR_HEIGHT, UNUSED /* fifth point */
		};

		/* Create the door mesh at the bottom center of the baseMesh object */
		Mesh doorMesh = new Mesh(WIDTH * 0.5f - DOOR_WIDTH * 0.5f, 0, doorBufferData, 5, DrawMode.LINE_STRIP,
				mEngine.getVertexBufferObjectManager());

		/* Set the door mesh to the color blue */
		doorMesh.setColor(org.andengine.util.adt.color.Color.BLUE);
		
		// Attach the door to the scene
		mScene.attachChild(doorMesh);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}