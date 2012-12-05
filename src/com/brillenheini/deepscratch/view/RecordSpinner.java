/*
 * Deep Scratch for Android
 * Copyright (C) 2010, 2011, 2012 Stefan Schweizer
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

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.ImageView;

import com.brillenheini.deepscratch.log.LL;

/**
 * Spin the image of the record with an image matrix.
 */
class RecordSpinner implements Callback {
	public static final int OFFSET_DEFAULT = -1;

	private static final int ROTATION_DELAY = 60;
	private static final int ROTATION_ANGLE = 10;

	private static final int WHAT_ROTATE = 0;

	private ImageView mImage;
	private Matrix mMatrix;
	private Handler mRotator = new Handler(this);

	private int mPivotX;
	private int mPivotY;

	private float mLastAngle;

	private boolean mStartDelayed = false;

	public RecordSpinner(ImageView image) {
		mImage = image;
	}

	public void setup(int offsetX, int offsetY) {
		final int translateX;
		final int translateY;
		final int imageSize = mImage.getDrawable().getIntrinsicWidth();

		if (offsetX == OFFSET_DEFAULT)
			translateX = -(imageSize - mImage.getWidth()) / 2;
		else
			translateX = mImage.getWidth() - offsetX - imageSize / 2;

		if (offsetY == OFFSET_DEFAULT)
			translateY = -(imageSize - mImage.getHeight()) / 2;
		else
			translateY = mImage.getHeight() - offsetY - imageSize / 2;

		mPivotX = imageSize / 2 + translateX;
		mPivotY = imageSize / 2 + translateY;

		if (LL.isDebugEnabled())
			LL.debug(mImage.getWidth() + "x" + mImage.getHeight()
					+ " imageSize=" + imageSize + " translateX=" + translateX
					+ " translateY=" + translateY);

		mMatrix = new Matrix();
		mMatrix.setTranslate(translateX, translateY);
		mImage.setImageMatrix(mMatrix);

		// startRotation has alredy been called, start rotation now
		if (mStartDelayed)
			rotateOnce();
	}

	/**
	 * Spin the record by the specified amount of degrees.
	 */
	public void spin(float degrees) {
		mMatrix.postRotate(degrees, mPivotX, mPivotY);
		mImage.setImageMatrix(mMatrix);
		mLastAngle = degrees;
	}

	/**
	 * Spin the record according to the scratched distance. The calculation of
	 * the angle uses a right angle triangle for simplicity.
	 * 
	 * @param dy
	 *            scratch distance on y-axis
	 * @param x
	 *            starting point of scratch on x-axis
	 */
	public void spin(float dy, float x) {
		float b = x - mPivotX;
		if (b != 0) {
			// atan returns radians
			float angle = (float) toDegrees(atan(dy / b));
			spin(angle);
		}
	}

	/**
	 * Start rotating the record.
	 */
	public void startRotation() {
		if (mMatrix != null)
			rotateOnce();
		else
			mStartDelayed = true;
	}

	/**
	 * Stop rotating the record.
	 */
	public void stopRotation() {
		mRotator.removeMessages(WHAT_ROTATE);
	}

	private void rotateOnce() {
		int degrees = ROTATION_ANGLE;

		// Pulling back
		if (mLastAngle < -1)
			degrees = (int) (mLastAngle * 0.4f - 0.5f);

		mRotator.removeMessages(WHAT_ROTATE);
		mRotator.sendMessageDelayed(
				mRotator.obtainMessage(WHAT_ROTATE, degrees, 0), ROTATION_DELAY);
	}

	@Override
	public boolean handleMessage(Message msg) {
		spin(msg.arg1);
		rotateOnce();
		return true;
	}
}
