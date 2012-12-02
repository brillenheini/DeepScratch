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
package com.brillenheini.deepscratch.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.brillenheini.deepscratch.log.LL;
import com.brillenheini.deepscratch.view.Converter;

public class ScratchSoundPool {
	private static final int VELOCITY_MIN = Converter.dipsToPix(100);
	private static final int VELOCITY_MID = Converter.dipsToPix(800);
	private static final int VELOCITY_MAX = Converter.dipsToPix(3000);
	private static final float PITCH_MIN = 0.5f;
	private static final float PITCH_MID = 1.0f;
	private static final float PITCH_MAX = 2.0f;

	private SoundPool mPool;

	// samples
	private int mSampleID = -1;
	private int mForwardID = -1;
	private int mBackwardID = -1;

	public ScratchSoundPool() {
		mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	}

	public void loadSample(Context context, Sample sample) {
		unloadSound(mSampleID);
		unloadSound(mForwardID);
		unloadSound(mBackwardID);
		mSampleID = mPool.load(context, sample.getSampleID(), 1);
		mForwardID = mPool.load(context, sample.getForwardID(), 1);
		mBackwardID = mPool.load(context, sample.getBackwardID(), 1);
	}

	private void unloadSound(int soundID) {
		if (soundID != -1)
			mPool.unload(soundID);
	}

	public void playSample() {
		playSound(mSampleID, PITCH_MID);
	}

	public void playForward(float velocity) {
		playSound(mForwardID, calculatePitch(velocity));
	}

	public void playBackward(float velocity) {
		playSound(mBackwardID, calculatePitch(velocity));
	}

	private void playSound(int soundID, float pitch) {
		if (LL.isDebugEnabled())
			LL.debug("playSound: " + soundID + " pitch=" + pitch);
		mPool.play(soundID, 1, 1, 0, 0, pitch);
	}

	/**
	 * @param velocity
	 *            in pixels per second
	 * @return sample pitch
	 * @see <a href="http://de.wikipedia.org/wiki/Gerade">Gerade</a>
	 */
	private static float calculatePitch(float velocity) {
		float pitch;
		if (velocity <= VELOCITY_MIN)
			pitch = PITCH_MIN;
		else if (velocity <= VELOCITY_MID)
			pitch = (PITCH_MID - PITCH_MIN) / (VELOCITY_MID - VELOCITY_MIN)
					* (velocity - VELOCITY_MIN) + PITCH_MIN;
		else if (velocity <= VELOCITY_MAX)
			pitch = (PITCH_MAX - PITCH_MID) / (VELOCITY_MAX - VELOCITY_MID)
					* (velocity - VELOCITY_MID) + PITCH_MID;
		else
			pitch = PITCH_MAX;
		return pitch;
	}

	public void close() {
		mPool.release();
		mPool = null;
	}
}
