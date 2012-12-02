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

import java.util.List;

/**
 * A scratch sample. Used to load sounds and build the selection menu.
 */
public final class Sample {
	private String mName;
	private int mSampleID;
	private int mForwardID;
	private int mBackwardID;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            sample name, displayed in menu, not translated
	 * @param sampleID
	 *            resource ID
	 * @param forwardID
	 *            resource ID
	 * @param backwardID
	 *            resource ID
	 */
	public Sample(String name, int sampleID, int forwardID, int backwardID) {
		mName = name;
		mSampleID = sampleID;
		mForwardID = forwardID;
		mBackwardID = backwardID;
	}

	public String getName() {
		return mName;
	}

	public int getSampleID() {
		return mSampleID;
	}

	public int getForwardID() {
		return mForwardID;
	}

	public int getBackwardID() {
		return mBackwardID;
	}

	/**
	 * Find a sample in a list.
	 * 
	 * @param samples
	 *            the list
	 * @param name
	 *            name of the sample to find
	 * @return the index of the sample in the list or 0 if the sample cannot be
	 *         found
	 */
	public static int findSample(List<Sample> samples, String name) {
		for (int i = 0; i < samples.size(); i++)
			if (samples.get(i).getName().equals(name))
				return i;
		return 0;
	}

	@Override
	public String toString() {
		return mName;
	}
}
