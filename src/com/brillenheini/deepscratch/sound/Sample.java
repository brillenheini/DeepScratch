// Copyright (C) 2010, 2011 Stefan Schweizer <steve.schweizer@gmail.com>
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
