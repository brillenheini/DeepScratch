// Copyright (C) 2010, 2011 Stefan Schweizer <steve.schweizer@gmail.com>

package com.brillenheini.deepscratch.view;

import android.content.Context;

import com.brillenheini.deepscratch.log.LL;

public final class Converter {
	private static float density;

	private Converter() {
	}

	public static void initialize(Context context) {
		density = context.getResources().getDisplayMetrics().density;
		if (LL.isDebugEnabled())
			LL.debug("display density=" + density);
	}

	/**
	 * Convert dips to pixels.
	 * 
	 * @see <a
	 *      href="http://developer.android.com/guide/practices/screens_support.html#dips-pels"
	 *      >Converting from dips to pixels</a>
	 */
	public static int dipsToPix(float dips) {
		return (int) (dips * density + 0.5f);
	}
}
