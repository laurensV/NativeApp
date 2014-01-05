package uva.verspeek.hearthstone.controllers;

import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;

import uva.verspeek.hearthstone.activities.GameActivity;


public class ZoomScrollController implements IPinchZoomDetectorListener,
IScrollDetectorListener {
	static GameActivity gameScreen;
	
	public PinchZoomDetector mPinchZoomDetector;
	public float mInitialTouchZoomFactor;
	public SurfaceScrollDetector mScrollDetector;
	
	public ZoomScrollController(GameActivity gameScreen) {
		ZoomScrollController.gameScreen = gameScreen;
		
		/*
		 * Create and set the zoom detector to listen for touch events using
		 * this activity's listener
		 */
		mPinchZoomDetector = new PinchZoomDetector(this);
		mScrollDetector = new SurfaceScrollDetector(this);

		// Enable the zoom detector
		mPinchZoomDetector.setEnabled(true);
	}
	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {

		/*
		 * On every sub-sequent touch event (after the initial touch) we offset
		 * the initial camera zoom factor by the zoom factor calculated by
		 * pinch-zooming
		 */
		final float newZoomFactor = mInitialTouchZoomFactor * pZoomFactor;

		// If the camera is within zooming bounds
		if (newZoomFactor < 2.5f && newZoomFactor > 0.95f) {
			// Set the new zoom factor
			gameScreen.mSmoothCamera.setZoomFactor(newZoomFactor);
		}
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {

	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pSceneTouchEvent) {
		// On first detection of pinch zooming, obtain the initial zoom factor
		mInitialTouchZoomFactor = gameScreen.mSmoothCamera.getZoomFactor();
	}

	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = gameScreen.mSmoothCamera.getZoomFactor();
		gameScreen.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY
				/ zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = gameScreen.mSmoothCamera.getZoomFactor();
		gameScreen.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY
				/ zoomFactor);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = gameScreen.mSmoothCamera.getZoomFactor();
		gameScreen.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY
				/ zoomFactor);
	}
}
