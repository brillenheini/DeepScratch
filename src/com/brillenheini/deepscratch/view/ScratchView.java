/*
 * Deep Scratch for Android
 * Copyright (C) 2010 Stefan Schweizer
 *
 * This file is part of Deep Scratch.
 *
 * Deep Scratch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Scratch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Deep Scratch.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	private float mLastX;
	private float mLastY;
	private float mLastDX;
	private float mLastDY;
	private float mStartX;
	private float mStartY;

	private long mLastTime;
	private long mStartTime;

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
			mLastX = event.getX();
			mLastY = event.getY();
			mLastDX = 0;
			mLastDY = 0;
			mStartX = mLastX;
			mStartY = mLastY;
			mLastTime = event.getEventTime();
			mStartTime = mLastTime;
			mPlayed = false;
			mSpinner.stopRotation();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final float x = event.getX();
			final float y = event.getY();
			final float dx = x - mLastX;
			final float dy = y - mLastY;

			if (dy > 0 && mLastDY < 0 || dy < 0 && mLastDY > 0) {
				mStartY = mLastY;
				mStartTime = mLastTime;
				mPlayed = false;
			}
			if (dx > 0 && mLastDX < 0 || dx < 0 && mLastDX > 0) {
				mStartX = mLastX;
				mPlayed = false;
			}

			if (!mPlayed) {
				final float scratchDistance = Math.abs(y - mStartY);
				final float lastScratchDistance = Math.abs(mLastY - mStartY);
				final float sampleDistance = Math.abs(x - mStartX);
				final float lastSampleDistance = Math.abs(mLastX - mStartX);

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

			mSpinner.spin(dy, mLastX);

			mLastX = x;
			mLastY = y;
			if (dx != 0)
				mLastDX = dx;
			if (dy != 0)
				mLastDY = dy;
			mLastTime = event.getEventTime();
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
		float dt = (float) (event.getEventTime() - mStartTime) / 1000;
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
