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

import com.brillenheini.deepscratch.log.LL;

public final class Converter {
	private static float sDensity;

	private Converter() {
	}

	public static void initialize(Context context) {
		sDensity = context.getResources().getDisplayMetrics().density;
		if (LL.isDebugEnabled())
			LL.debug("display density=" + sDensity);
	}

	/**
	 * Convert dips to pixels.
	 * 
	 * @see <a
	 *      href="http://developer.android.com/guide/practices/screens_support.html#dips-pels"
	 *      >Converting from dips to pixels</a>
	 */
	public static int dipsToPix(float dips) {
		return (int) (dips * sDensity + 0.5f);
	}
}
