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
package com.brillenheini.deepscratch.log;

import android.util.Log;

/**
 * Little logger (LL). This class wraps Android's logging API because it is
 * unhandy to use.
 */
public final class LL {
	public static final String TAG = "DeepScratch";

	private LL() {
	}

	public static void verbose(String msg) {
		Log.v(TAG, msg);
	}

	public static void verbose(String msg, Throwable tr) {
		Log.v(TAG, msg, tr);
	}

	public static boolean isVerboseEnabled() {
		return Log.isLoggable(TAG, Log.VERBOSE);
	}

	public static void debug(String msg) {
		Log.d(TAG, msg);
	}

	public static void debug(String msg, Throwable tr) {
		Log.d(TAG, msg, tr);
	}

	public static boolean isDebugEnabled() {
		return Log.isLoggable(TAG, Log.DEBUG);
	}

	public static void info(String msg) {
		Log.i(TAG, msg);
	}

	public static void info(String msg, Throwable tr) {
		Log.i(TAG, msg, tr);
	}

	public static boolean isInfoEnabled() {
		return Log.isLoggable(TAG, Log.INFO);
	}

	public static void warn(String msg) {
		Log.w(TAG, msg);
	}

	public static void warn(String msg, Throwable tr) {
		Log.w(TAG, msg, tr);
	}

	public static boolean isWarnEnabled() {
		return Log.isLoggable(TAG, Log.WARN);
	}

	public static void error(String msg) {
		Log.e(TAG, msg);
	}

	public static void error(String msg, Throwable tr) {
		Log.e(TAG, msg, tr);
	}
}
