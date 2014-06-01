package com.sinaapp.thesnake;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.tendcloud.tenddata.TCAgent;

public class GameActivity extends Activity {
	private static final String GAME_SAVE = "game_save";

	private SoundPool mSoundPool;
	private int mBackGroundId;
	private int mNormalId;
	private int mAccId;
	private int mDecId;
	private int mLoseId;
	private int mBackGroundStreamId;

	private boolean mIsBackStreamPaused = false;

	public GameView mGameView;

	public static final int STREAM_NORMAL = 0;
	public static final int STREAM_ACC = 1;
	public static final int STREAM_DEC = 2;
	public static final int STREAM_LOSE = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("zzz", "GameActivity onCreate");

		// No Title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.snake_layout);

		mGameView = (GameView) findViewById(R.id.snake);

		mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		mBackGroundId = mSoundPool.load(this, R.raw.bg, 0);
		mNormalId = mSoundPool.load(this, R.raw.normal, 0);
		mAccId = mSoundPool.load(this, R.raw.acc, 0);
		mDecId = mSoundPool.load(this, R.raw.dec, 0);
		mLoseId = mSoundPool.load(this, R.raw.lose, 0);

		mSoundPool
				.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool,
							int sampleId, int status) {
						if (sampleId == mAccId) {
							mBackGroundStreamId = mSoundPool.play(mAccId, 1.0f, 1.0f,
									0, -1, 1);
						}
					}
				});
	}

	@Override
	protected void onPause() {
		Log.i("zzz", "GameActivity onPause");
		super.onPause();
		TCAgent.onPause(this);
		
		if(mSoundPool != null && mBackGroundStreamId > 0) {
			mSoundPool.pause(mBackGroundStreamId);
			mIsBackStreamPaused = true;
		}

		int mode = mGameView.getMode();

		SharedPreferences gameSavePref = getSharedPreferences(GAME_SAVE, 0);
		SharedPreferences.Editor editor = gameSavePref.edit();

		editor.putInt("MODE", mode);
		if (mode != GameView.LOSE) {
			Bundle map = mGameView.saveState();

			editor.putString("mAppleList",
					arrayListToString(map.getIntArray("mAppleList")));
			editor.putInt("mDirection", map.getInt("mDirection"));
			editor.putInt("mNextDirection", map.getInt("mNextDirection"));
			editor.putLong("mMoveDelay", map.getLong("mMoveDelay"));
			editor.putLong("mScore", map.getLong("mScore"));
			editor.putString("mSnakeTrail",
					arrayListToString(map.getIntArray("mSnakeTrail")));
			editor.putLong("mSpecDuration", map.getLong("mSpecDuration"));
			editor.putInt("mLastSpecAppleEat", map.getInt("mLastSpecAppleEat"));

			Log.i("zzz",
					"GameActivity record move delay is "
							+ map.getLong("mMoveDelay"));
		}

		editor.commit();

		mGameView.setMode(GameView.PAUSE);
	}

	@Override
	protected void onResume() {
		Log.i("zzz", "GameActivity onResume");
		super.onResume();
		TCAgent.onResume(this);

		if (mSoundPool != null && mBackGroundStreamId > 0 && mIsBackStreamPaused) {
			mSoundPool.resume(mBackGroundStreamId);
		}

		SharedPreferences gameSavePref = getSharedPreferences(GAME_SAVE, 0);

		int mode = gameSavePref.getInt("MODE", GameView.LOSE);

		Log.i("zzz", "GameActivity save mode is " + mode);

		if (mode != GameView.LOSE) {
			Bundle map = new Bundle();

			map.putIntArray("mAppleList",
					stringToArrayList(gameSavePref.getString("mAppleList", "")));
			map.putInt("mDirection", gameSavePref.getInt("mDirection", 0));
			map.putInt("mNextDirection",
					gameSavePref.getInt("mNextDirection", 0));
			map.putLong("mMoveDelay", gameSavePref.getLong("mMoveDelay", 0));
			map.putLong("mScore", gameSavePref.getLong("mScore", 0));
			map.putIntArray(
					"mSnakeTrail",
					stringToArrayList(gameSavePref.getString("mSnakeTrail", "")));
			map.putLong("mSpecDuration",
					gameSavePref.getLong("mSpecDuration", 0));
			map.putInt("mLastSpecAppleEat",
					gameSavePref.getInt("mLastSpecAppleEat", 0));

			mGameView.restoreState(map);
		}
		// mGameView.setMode(GameView.PAUSE);
	}

	@Override
	protected void onStop() {
		Log.i("zzz", "GameActivity onStop");
		super.onStop();

		if (mSoundPool != null) {
			mSoundPool.release();
			mSoundPool = null;
		}
	}

	private String arrayListToString(int[] array) {
		String result = "";

		for (int i : array) {
			result += i + ",";
		}
		return result;
	}

	private int[] stringToArrayList(String str) {
		String[] sa = str.split(",");

		int[] array = new int[sa.length];

		for (int i = 0; i < sa.length; i++) {
			array[i] = Integer.parseInt(sa[i]);
		}

		return array;
	}

	public void play(int id) {
		switch (id) {
		case STREAM_NORMAL:
			mSoundPool.play(mNormalId, 1.0f, 1.0f, 0, 0, 1);
			break;
		case STREAM_ACC:
			mSoundPool.play(mAccId, 1.0f, 1.0f, 0, 0, 1);
			break;
		case STREAM_DEC:
			mSoundPool.play(mDecId, 1.0f, 1.0f, 0, 0, 1);
			break;
		case STREAM_LOSE:
			mSoundPool.play(mLoseId, 1.0f, 1.0f, 0, 0, 1);
			break;
		}
	}

	public void setBackGroundStreamRate(float rate) {
		Log.i("zzz", "set rate to " + rate);
		if (mSoundPool != null && mBackGroundStreamId > 0) {
			mSoundPool.setRate(mBackGroundStreamId, rate);
		}
	}

	// @Override
	// protected void onStop() {
	// Log.i("zzz", "GameActivity onStop");
	// super.onStop();
	//
	// mGameView.stop();
	// }

	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// Log.i("zzz", "GameActivity onSaveInstance");
	// //Store the game state
	// outState.putBundle(ICICLE_KEY, mGameView.saveState());
	// }

	// @Override
	// protected void onResume() {
	//
	// }
	//
	// @Override
	// protected void onRestoreInstanceState (Bundle savedInstanceState) {
	//
	// }
}
