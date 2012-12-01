// Copyright (C) 2010, 2011 Stefan Schweizer <steve.schweizer@gmail.com>

package com.brillenheini.deepscratch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.brillenheini.deepscratch.free.R;
import com.brillenheini.deepscratch.log.LL;
import com.brillenheini.deepscratch.sound.Sample;
import com.brillenheini.deepscratch.sound.ScratchSoundPool;
import com.brillenheini.deepscratch.view.Converter;
import com.brillenheini.deepscratch.view.ScratchView;

/**
 * @author Stefan Schweizer
 */
public class DeepScratch extends Activity {
	private static final int PICK_SONG = 1;
	private static final int ITEM_ID_SAMPLE = 1;
	protected static final int DIALOG_HELP = 1;

	private static final String STATE_SAMPLE = "STATE_SAMPLE";
	private static final String STATE_URI = "STATE_URI";
	private static final String STATE_POSITION = "STATE_POSITION";
	private static final String STATE_PAUSED = "STATE_PAUSED";

	private static final float MEDIA_VOLUME = 0.75f;

	// Sample and media playback, saved as instance state
	private int mSelectedSample = 0;
	private Uri mUri = null;
	private int mPosition = 0;
	private boolean mPaused = false;

	private List<Sample> mSamples;
	private ScratchSoundPool mSounds;
	private ScratchView mScratchView;
	private MediaPlayer mPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Converter.initialize(this);
		setContentView(R.layout.main);

		// Try to restore instance state
		if (savedInstanceState != null) {
			mSelectedSample = savedInstanceState.getInt(STATE_SAMPLE);
			String uri = savedInstanceState.getString(STATE_URI);
			if (uri != null) {
				mUri = Uri.parse(uri);
				mPosition = savedInstanceState.getInt(STATE_POSITION);
				mPaused = savedInstanceState.getBoolean(STATE_PAUSED);
			}
			if (LL.isDebugEnabled())
				LL.debug("Restoring: sample=" + mSelectedSample + " uri=" + uri
						+ " position=" + mPosition + " paused=" + mPaused);
		}
		mSamples = new ArrayList<Sample>();
		addSamples(mSamples);

		mSounds = new ScratchSoundPool();
		mSounds.loadSample(this, mSamples.get(mSelectedSample));

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mScratchView = (ScratchView) findViewById(R.id.scratch);
		mScratchView.setScratchSoundPool(mSounds);
	}

	/**
	 * Add available samples. The first sample is loaded on startup.
	 */
	private void addSamples(List<Sample> l) {
		l.add(new Sample("Uuh", R.raw.uuh, R.raw.uuh_fw, R.raw.uuh_bw));
		l.add(new Sample("Bass", R.raw.bass, R.raw.bass_fw, R.raw.bass_bw));
		l.add(new Sample("Fresh", R.raw.fresh, R.raw.fresh_fw, R.raw.fresh_bw));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SAMPLE, mSelectedSample);
		if (mUri != null) {
			outState.putString(STATE_URI, mUri.toString());
			outState.putInt(STATE_POSITION, mPlayer.getCurrentPosition());
			outState.putBoolean(STATE_PAUSED, mPaused);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mUri != null)
			preparePlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mScratchView.startRotation();
		if (mPlayer != null && !mPaused)
			mPlayer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mScratchView.stopRotation();
		if (mPlayer != null && mPlayer.isPlaying())
			mPlayer.pause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		closePlayer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSounds.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_HELP:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.help);
			dialog.setTitle(R.string.app_name);

			// Make links clickable
			TextView text = (TextView) dialog.findViewById(R.id.help_thanks);
			text.setMovementMethod(LinkMovementMethod.getInstance());
			text = (TextView) dialog.findViewById(R.id.help_attributions);
			text.setMovementMethod(LinkMovementMethod.getInstance());
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_menu, menu);

		// Add samples to submenu
		if (mSamples.size() > 1) {
			MenuItem menuSample = menu.findItem(R.id.menu_sample);
			menuSample.setVisible(true);
			SubMenu subMenuSample = menuSample.getSubMenu();
			for (Sample sample : mSamples)
				subMenuSample.add(R.id.menu_sample_group, ITEM_ID_SAMPLE,
						Menu.NONE, sample.getName());
			subMenuSample.setGroupCheckable(R.id.menu_sample_group, true, true);
			subMenuSample.getItem(mSelectedSample).setChecked(true);
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (mPlayer != null) {
			menu.findItem(R.id.menu_pause).setVisible(!mPaused);
			menu.findItem(R.id.menu_play).setVisible(mPaused);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_music) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("audio/*");
			try {
				startActivityForResult(intent, PICK_SONG);
			} catch (ActivityNotFoundException anfe) {
				toastError(R.string.error_noactivity, anfe);
			}
			return true;
		} else if (itemId == R.id.menu_pause) {
			mPlayer.pause();
			mPaused = true;
			return true;
		} else if (itemId == R.id.menu_play) {
			mPlayer.start();
			mPaused = false;
			return true;
		} else if (itemId == R.id.menu_help) {
			showDialog(DIALOG_HELP);
			return true;
		} else if (itemId == R.id.menu_buy) {
			Intent buyIntent = new Intent(Intent.ACTION_VIEW, getMarketUri());
			try {
				startActivity(buyIntent);
			} catch (ActivityNotFoundException anfe) {
				toastError(R.string.error_nomarket, anfe);
			}
			return true;
		} else if (itemId == ITEM_ID_SAMPLE) {
			if (!item.isChecked()) {
				item.setChecked(true);
				mSelectedSample = Sample.findSample(mSamples, item.getTitle()
						.toString());
				mSounds.loadSample(this, mSamples.get(mSelectedSample));
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_SONG:
			if (resultCode == RESULT_OK) {
				mUri = data.getData();
				mPosition = 0;
				mPaused = false;
				preparePlayer();
			}
			break;
		}
	}

	protected void toastError(int id, Throwable tr) {
		CharSequence msg = getResources().getText(id);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		LL.error(msg.toString(), tr);
	}

	private Uri getMarketUri() {
		return Uri
				.parse("market://details?id=com.brillenheini.deepscratch.paid");
	}

	private void preparePlayer() {
		MediaPlayer player = mPlayer;
		if (player == null) {
			player = new MediaPlayer();
		} else {
			player.reset();
		}
		try {
			player.setDataSource(this, mUri);
			player.prepare();
			player.setLooping(true);
			player.setVolume(MEDIA_VOLUME, MEDIA_VOLUME);
			if (mPosition > 0)
				player.seekTo(mPosition);
		} catch (IOException ioe) {
			LL.error("Error starting playback of " + mUri, ioe);
		} finally {
			mPlayer = player;
		}
	}

	private void closePlayer() {
		if (mPlayer != null) {
			mPosition = mPlayer.getCurrentPosition();
			mPlayer.release();
			mPlayer = null;
		}
	}
}