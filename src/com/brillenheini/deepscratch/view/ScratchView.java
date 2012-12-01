// Copyright (C) 2010, 2011 Stefan Schweizer <steve.schweizer@gmail.com>

package com.brillenheini.deepscratch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.brillenheini.deepscratch.sound.ScratchSoundPool;

public class ScratchView extends ImageView {
	private ScratchSoundPool mSounds;
	private RecordSpinner mSpinner;

	private static final String LAYOUT_OFFSET_X = "offsetX";
	private static final String LAYOUT_OFFSET_Y = "offsetY";
	private int mOffsetX;
	private int mOffsetY;

	private static final int MIN_SCRATCH_DISTANCE = Converter.dipsToPix(50);
	private static final int MIN_SAMPLE_DISTANCE = Converter.dipsToPix(80);
	private float lastX;
	private float lastY;
	private float lastDX;
	private float lastDY;
	private float startX;
	private float startY;

	private long lastTime;
	private long startTime;

	private boolean mPlayed;

	public ScratchView(Context context) {
		this(context, null, 0);
	}

	public ScratchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScratchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mOffsetX = getLayoutAttribute(attrs, LAYOUT_OFFSET_X);
		mOffsetY = getLayoutAttribute(attrs, LAYOUT_OFFSET_Y);
		mSpinner = new RecordSpinner(this);
	}

	private int getLayoutAttribute(AttributeSet attrs, String attribute) {
		int value = attrs.getAttributeIntValue(null, attribute,
				RecordSpinner.OFFSET_DEFAULT);
		if (value != RecordSpinner.OFFSET_DEFAULT)
			value = Converter.dipsToPix(value);
		return value;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Setup RecordSpinner when the size is known
		mSpinner.setup(mOffsetX, mOffsetY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			lastX = event.getX();
			lastY = event.getY();
			lastDX = 0;
			lastDY = 0;
			startX = lastX;
			startY = lastY;
			lastTime = event.getEventTime();
			startTime = lastTime;
			mPlayed = false;
			mSpinner.stopRotation();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final float x = event.getX();
			final float y = event.getY();
			final float dx = x - lastX;
			final float dy = y - lastY;

			if (dy > 0 && lastDY < 0 || dy < 0 && lastDY > 0) {
				startY = lastY;
				startTime = lastTime;
				mPlayed = false;
			}
			if (dx > 0 && lastDX < 0 || dx < 0 && lastDX > 0) {
				startX = lastX;
				mPlayed = false;
			}

			if (!mPlayed) {
				final float scratchDistance = Math.abs(y - startY);
				final float lastScratchDistance = Math.abs(lastY - startY);
				final float sampleDistance = Math.abs(x - startX);
				final float lastSampleDistance = Math.abs(lastX - startX);

				if (scratchDistance > MIN_SCRATCH_DISTANCE
						&& lastScratchDistance <= MIN_SCRATCH_DISTANCE) {
					if (dy < 0)
						mSounds.playForward(velocity(event, scratchDistance));
					else if (dy > 0)
						mSounds.playBackward(velocity(event, scratchDistance));
					mPlayed = true;
				} else if (sampleDistance > MIN_SAMPLE_DISTANCE
						&& lastSampleDistance <= MIN_SAMPLE_DISTANCE) {
					mSounds.playSample();
					mPlayed = true;
				}
			}

			mSpinner.spin(dy, lastX);

			lastX = x;
			lastY = y;
			if (dx != 0)
				lastDX = dx;
			if (dy != 0)
				lastDY = dy;
			lastTime = event.getEventTime();
			break;
		}

		case MotionEvent.ACTION_UP: {
			mSpinner.startRotation();
			break;
		}
		}

		return true;
	}

	/**
	 * Calculate scratch velocity in pixels per second shortly before a sample
	 * is played.
	 */
	private float velocity(MotionEvent event, float scratchDistance) {
		float dt = (float) (event.getEventTime() - startTime) / 1000;
		return scratchDistance / dt;
	}

	public void setScratchSoundPool(ScratchSoundPool sounds) {
		mSounds = sounds;
	}

	public void startRotation() {
		mSpinner.startRotation();
	}

	public void stopRotation() {
		mSpinner.stopRotation();
	}
}
