/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.snake;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * SnakeView: implementation of a simple game of Snake
 * 
 * 
 */
public class SnakeView extends SurfaceView implements Callback, Runnable {

	private static final String TAG = "SnakeView";

	private static final int TIME_IN_FRAME = 50;
	/**
	 * Current mode of application: READY to run, RUNNING, or you have already
	 * lost. static final ints are used instead of an enum for performance
	 * reasons.
	 */
	private int mMode = READY;
	public static final int PAUSE = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;

	/**
	 * Current direction the snake is headed.
	 */
	private int mDirection = NORTH;
	private int mNextDirection = NORTH;
	private static final int NORTH = 1;
	private static final int SOUTH = 2;
	private static final int EAST = 3;
	private static final int WEST = 4;

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int RED_STAR = 1;
	private static final int YELLOW_STAR = 2;
	private static final int GREEN_STAR = 3;

	/**
	 * mScore: used to track the number of apples captured mMoveDelay: number of
	 * milliseconds between snake movements. This will decrease as apples are
	 * captured.
	 */
	private long mScore = 0;
	private long mMoveDelay = 100;
	private long mNextMoveDalay = 100;
	/**
	 * mLastMove: tracks the absolute time when the snake last moved, and is
	 * used to determine if a move should be made based on mMoveDelay.
	 */
	private long mLastMove;

	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;

	/**
	 * mSnakeTrail: a list of Coordinates that make up the snake's body
	 * mAppleList: the secret location of the juicy apples the snake craves.
	 */
	private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();

	/**
	 * Everyone needs a little randomness in their life
	 */
	private static final Random RNG = new Random();

	private float mPressPointX;
	private float mPressPointY;
	
	private static int mControlMode;

	protected static int mTileSize;

	protected static int mXTileCount;
	protected static int mYTileCount;

	protected static int mWidth;
	protected static int mHeight;

	private static int mXOffset;
	private static int mYOffset;

	private Bitmap[] mTileArray;

	private int[][] mTileGrid;

	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintR = new Paint(Paint.ANTI_ALIAS_FLAG);

	private SurfaceHolder mSurfaceHolder = null;

	private boolean mIsRunning = false;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	/**
	 * Constructs a SnakeView based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public SnakeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TileView);

		mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);

		a.recycle();

		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);

		initSnakeView();
	}

	public SnakeView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TileView);

		mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);

		a.recycle();

		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);

		initSnakeView();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;

		mXTileCount = (int) Math.floor(w / mTileSize);
		mYTileCount = (int) Math.floor(h / mTileSize);

		mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
		mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

		mTileGrid = new int[mXTileCount][mYTileCount];
		clearTiles();

		mPaint.setColor(Color.rgb(29, 56, 13));
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setFakeBoldText(true);
		mPaint.setTextSize(12);
		mPaintR.setColor(Color.rgb(29, 56, 13));
		mPaintR.setTextAlign(Paint.Align.RIGHT);
	}

	private void initSnakeView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(4);
		loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
		loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar1));
		loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));

		mControlMode = 1;
	}

	public void resetTiles(int tilecount) {
		mTileArray = new Bitmap[tilecount];
	}

	public void loadTile(int key, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, mTileSize, mTileSize);
		tile.draw(canvas);

		mTileArray[key] = bitmap;
	}

	public void clearTiles() {
		for (int x = 0; x < mXTileCount; x++) {
			for (int y = 0; y < mYTileCount; y++) {
				setTile(0, x, y);
			}
		}
	}

	public void setTile(int tileindex, int x, int y) {
		mTileGrid[x][y] = tileindex;
	}

	public void draw(Canvas canvas) {
		canvas.drawARGB(255, 151, 192, 62);

		/** draw the bounder */
		for (int x = 1; x < mXTileCount - 1; x++) {
			canvas.drawBitmap(mTileArray[3], mXOffset + x * mTileSize,
					mYOffset, mPaint);
			canvas.drawBitmap(mTileArray[3], mXOffset + x * mTileSize, mYOffset
					+ 3 * mTileSize, mPaint);
			canvas.drawBitmap(mTileArray[3], mXOffset + x * mTileSize, mYOffset
					+ (mYTileCount - 1) * mTileSize, mPaint);
		}
		for (int y = 0; y < mYTileCount; y += 1) {
			canvas.drawBitmap(mTileArray[3], mXOffset,
					mYOffset + y * mTileSize, mPaint);
			canvas.drawBitmap(mTileArray[3], mXOffset + (mXTileCount - 1)
					* mTileSize, mYOffset + y * mTileSize, mPaint);
		}
		canvas.drawBitmap(mTileArray[3], mXOffset + ((int) mXTileCount * 2 / 3)
				* mTileSize, mYOffset + mTileSize, mPaint);
		canvas.drawBitmap(mTileArray[3], mXOffset + ((int) mXTileCount * 2 / 3)
				* mTileSize, mYOffset + mTileSize * 2, mPaint);

		FontMetrics fontMetrics = mPaint.getFontMetrics();
		canvas.drawText(getContext().getResources().getString(R.string.snakeview_score), mXOffset + mTileSize * 2, mYOffset + mTileSize * 3 - 5 /*((mTileSize * 2 - (fontMetrics.bottom - fontMetrics.top)) / 2)*/, mPaint);
		canvas.drawText(mScore + "", mXOffset + ((int) mXTileCount * 2 / 3) * mTileSize - mTileSize, mYOffset + mTileSize * 3 - ((mTileSize * 2 - (fontMetrics.bottom - fontMetrics.top)) / 2), mPaintR);

		switch(mMode) {
		case READY:
			break;
		case PAUSE:
			break;
		case LOSE:
			break;
		case RUNNING:
			for (int x = 1; x < mXTileCount - 1; x += 1) {
				for (int y = 1; y < mYTileCount - 1; y += 1) {
					if (mTileGrid[x][y] > 0) {
						canvas.drawBitmap(mTileArray[mTileGrid[x][y]], mXOffset + x
								* mTileSize, mYOffset + y * mTileSize, mPaint);
					}
				}
			}
			break;
		}
	}

	private void initNewGame() {
		mSnakeTrail.clear();
		mAppleList.clear();

		// For now we're just going to load up a short default eastbound snake
		// that's just turned north

		mSnakeTrail.add(new Coordinate(7, 7));
		mSnakeTrail.add(new Coordinate(6, 7));
		mSnakeTrail.add(new Coordinate(5, 7));
		mSnakeTrail.add(new Coordinate(4, 7));
		mSnakeTrail.add(new Coordinate(3, 7));
		mSnakeTrail.add(new Coordinate(2, 7));
		mNextDirection = EAST;

		// Two apples to start with
		addRandomApple();
		addRandomApple();

		mMoveDelay = 300;
		mNextMoveDalay = 300;
		mScore = 0;
	}

	/**
	 * Given a ArrayList of coordinates, we need to flatten them into an array
	 * of ints before we can stuff them into a map for flattening and storage.
	 * 
	 * @param cvec
	 *            : a ArrayList of Coordinate objects
	 * @return : a simple array containing the x/y values of the coordinates as
	 *         [x1,y1,x2,y2,x3,y3...]
	 */
	private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
		int count = cvec.size();
		int[] rawArray = new int[count * 2];
		for (int index = 0; index < count; index++) {
			Coordinate c = cvec.get(index);
			rawArray[2 * index] = c.x;
			rawArray[2 * index + 1] = c.y;
		}
		return rawArray;
	}

	/**
	 * Save game state so that the user does not lose anything if the game
	 * process is killed while we are in the background.
	 * 
	 * @return a Bundle with this view's state
	 */
	public Bundle saveState() {
		Bundle map = new Bundle();

		map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
		map.putInt("mDirection", Integer.valueOf(mDirection));
		map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
		map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
		map.putLong("mScore", Long.valueOf(mScore));
		map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));

		return map;
	}

	/**
	 * Given a flattened array of ordinate pairs, we reconstitute them into a
	 * ArrayList of Coordinate objects
	 * 
	 * @param rawArray
	 *            : [x1,y1,x2,y2,...]
	 * @return a ArrayList of Coordinates
	 */
	private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
		ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

		int coordCount = rawArray.length;
		for (int index = 0; index < coordCount; index += 2) {
			Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
			coordArrayList.add(c);
		}
		return coordArrayList;
	}

	/**
	 * Restore game state if our process is being relaunched
	 * 
	 * @param icicle
	 *            a Bundle containing the game state
	 */
	public void restoreState(Bundle icicle) {
		setMode(PAUSE);

		mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
		mDirection = icicle.getInt("mDirection");
		mNextDirection = icicle.getInt("mNextDirection");
		mMoveDelay = icicle.getLong("mMoveDelay");
		mScore = icicle.getLong("mScore");
		mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
	}

	/*
	 * handles key events in the game. Update the direction our snake is
	 * traveling based on the DPAD. Ignore events that would cause the snake to
	 * immediately turn back on itself.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// public boolean onKeyDown(int keyCode, KeyEvent msg) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mMode == RUNNING) {
				mPressPointX = event.getX();
				mPressPointY = event.getY();
			}
			return true;
		case MotionEvent.ACTION_UP:
			if (mMode == READY | mMode == LOSE) {
				/*
				 * At the beginning of the game, or the end of a previous one,
				 * we should start a new game.
				 */
				initNewGame();
				setMode(RUNNING);
				update();
			} else if (mMode == PAUSE) {
				/*
				 * If the game is merely paused, we should just continue where
				 * we left off.
				 */
				setMode(RUNNING);
				update();
			} else if (mMode == RUNNING) {
				int direction = 0;

				float currentX = event.getX();
				if (currentX == mPressPointX) {
					if (currentX < (mWidth / 2)) {
						direction = -1;
					} else if (currentX >= (mWidth / 2)) {
						direction = 1;
					}
				} else if (currentX < mPressPointX) {
					direction = -1;
				} else if (currentX > mPressPointX) {
					direction = 1;
				}

				switch (mDirection) {
				case NORTH:
					if (direction < 0) {
						mNextDirection = WEST;
					} else if (direction > 0) {
						mNextDirection = EAST;
					}
					break;
				case SOUTH:
					if (direction < 0) {
						mNextDirection = EAST;
					} else if (direction > 0) {
						mNextDirection = WEST;
					}
					break;
				case WEST:
					if (direction < 0) {
						mNextDirection = SOUTH;
					} else if (direction > 0) {
						mNextDirection = NORTH;
					}
					break;
				case EAST:
					if (direction < 0) {
						mNextDirection = NORTH;
					} else if (direction > 0) {
						mNextDirection = SOUTH;
					}
					break;
				}
			}

			break;
		}

		return super.onTouchEvent(event);

		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// if (mMode == READY | mMode == LOSE) {
		// /*
		// * At the beginning of the game, or the end of a previous one,
		// * we should start a new game.
		// */
		// initNewGame();
		// setMode(RUNNING);
		// update();
		// return (super.onTouchEvent(event));
		// }
		//
		// if (mMode == PAUSE) {
		// /*
		// * If the game is merely paused, we should just continue where
		// * we left off.
		// */
		// setMode(RUNNING);
		// update();
		// return (super.onTouchEvent(event));
		// }
		//
		// // if (mDirection != SOUTH) {
		// // mNextDirection = NORTH;
		// // }
		// // return (true);
		// }

		// if(event.getAction() == MotionEvent.)

		// if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
		// if (mDirection != NORTH) {
		// mNextDirection = SOUTH;
		// }
		// return (true);
		// }
		//
		// if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
		// if (mDirection != EAST) {
		// mNextDirection = WEST;
		// }
		// return (true);
		// }
		//
		// if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
		// if (mDirection != WEST) {
		// mNextDirection = EAST;
		// }
		// return (true);
		// }

		// return super.onTouchEvent(event);
	}

	/**
	 * Sets the TextView that will be used to give information (such as "Game
	 * Over" to the user.
	 * 
	 * @param newView
	 */
	public void setTextView(TextView newView) {
		mStatusText = newView;
	}

	/**
	 * Updates the current mode of the application (RUNNING or PAUSED or the
	 * like) as well as sets the visibility of textview for notification
	 * 
	 * @param newMode
	 */
	public void setMode(int newMode) {
		int oldMode = mMode;
		mMode = newMode;

		if (newMode == RUNNING & oldMode != RUNNING) {
			// mStatusText.setVisibility(View.INVISIBLE);
			// update();
			return;
		}

		Resources res = getContext().getResources();
		CharSequence str = "";
		if (newMode == PAUSE) {
			str = res.getText(R.string.mode_pause);
		}
		if (newMode == READY) {
			str = res.getText(R.string.mode_ready);
		}
		if (newMode == LOSE) {
			str = res.getString(R.string.mode_lose_prefix) + mScore
					+ res.getString(R.string.mode_lose_suffix);
		}

		// mStatusText.setText(str);
		// mStatusText.setVisibility(View.VISIBLE);
	}

	/**
	 * Selects a random location within the garden that is not currently covered
	 * by the snake. Currently _could_ go into an infinite loop if the snake
	 * currently fills the garden, but we'll leave discovery of this prize to a
	 * truly excellent snake-player.
	 * 
	 */
	private void addRandomApple() {
		Coordinate newCoord = null;
		boolean found = false;
		while (!found) {
			// Choose a new location for our apple
			int newX = 1 + RNG.nextInt(mXTileCount - 2);
			int newY = 4 + RNG.nextInt(mYTileCount - 5);
			newCoord = new Coordinate(newX, newY);

			// Make sure it's not already under the snake
			boolean collision = false;
			int snakelength = mSnakeTrail.size();
			for (int index = 0; index < snakelength; index++) {
				if (mSnakeTrail.get(index).equals(newCoord)) {
					collision = true;
				}
			}
			// if we're here and there's been no collision, then we have
			// a good location for an apple. Otherwise, we'll circle back
			// and try again
			found = !collision;
		}
		if (newCoord == null) {
			Log.e(TAG, "Somehow ended up with a null newCoord!");
		} else {
			mAppleList.add(newCoord);			
		}
	}

	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's
	 * location.
	 */
	public void update() {
		if (mMode == RUNNING) {
Log.i("zzz", "updating .... MODE is " + mMode);
			clearTiles();
			updateWalls();
			updateSnake();
			updateApples();
			// mRedrawHandler.sleep(mMoveDelay);
		}
	}

	/**
	 * Draws some walls.
	 * 
	 */
	private void updateWalls() {
		for (int x = 0; x < mXTileCount; x++) {
			setTile(GREEN_STAR, x, 0);
			setTile(GREEN_STAR, x, mYTileCount - 1);
		}
		for (int y = 1; y < mYTileCount - 1; y++) {
			setTile(GREEN_STAR, 0, y);
			setTile(GREEN_STAR, mXTileCount - 1, y);
		}
	}

	/**
	 * Draws some apples.
	 * 
	 */
	private void updateApples() {
		for (Coordinate c : mAppleList) {
			setTile(YELLOW_STAR, c.x, c.y);
		}
	}

	/**
	 * Figure out which way the snake is going, see if he's run into anything
	 * (the walls, himself, or an apple). If he's not going to die, we then add
	 * to the front and subtract from the rear in order to simulate motion. If
	 * we want to grow him, we don't subtract from the rear.
	 * 
	 */
	private void updateSnake() {
		boolean growSnake = false;

		// grab the snake by the head
		Coordinate head = mSnakeTrail.get(0);
		Coordinate newHead = new Coordinate(1, 1);

		mDirection = mNextDirection;

		switch (mDirection) {
		case EAST: {
			newHead = new Coordinate(head.x + 1, head.y);
			break;
		}
		case WEST: {
			newHead = new Coordinate(head.x - 1, head.y);
			break;
		}
		case NORTH: {
			newHead = new Coordinate(head.x, head.y - 1);
			break;
		}
		case SOUTH: {
			newHead = new Coordinate(head.x, head.y + 1);
			break;
		}
		}

		// Collision detection
		// For now we have a 1-square wall around the entire arena
		if ((newHead.x < 1) || (newHead.y < 4) || (newHead.x > mXTileCount - 2)
				|| (newHead.y > mYTileCount - 2)) {
			setMode(LOSE);
			return;
		}

		// Look for collisions with itself
		int snakelength = mSnakeTrail.size();
		for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
			Coordinate c = mSnakeTrail.get(snakeindex);
			if (c.equals(newHead)) {
				setMode(LOSE);
				return;
			}
		}

		// push a new head onto the ArrayList and pull off the tail
		mSnakeTrail.add(0, newHead);

		// Look for apples
		int applecount = mAppleList.size();
		for (int appleindex = 0; appleindex < applecount; appleindex++) {
			Coordinate c = mAppleList.get(appleindex);
			if (c.equals(newHead)) {
				mAppleList.remove(c);
				addRandomApple();

				mScore++;
				mMoveDelay *= 0.9;

				growSnake = true;
			}
		}

		// except if we want the snake to grow
		if (!growSnake) {
			mSnakeTrail.remove(mSnakeTrail.size() - 1);
		}

		int index = 0;
		for (Coordinate c : mSnakeTrail) {
Log.i("zzz", "snake size is " + mSnakeTrail.size() + ", index is " + index);
			if (index == 0) {
				setTile(YELLOW_STAR, c.x, c.y);
			} else {
				setTile(RED_STAR, c.x, c.y);
			}
			index++;
		}
	}

	/**
	 * Simple class containing two integer values and a comparison function.
	 * There's probably something I should use instead, but this was quick and
	 * easy to build.
	 * 
	 */
	private class Coordinate {
		public int x;
		public int y;

		public Coordinate(int newX, int newY) {
			x = newX;
			y = newY;
		}

		public boolean equals(Coordinate other) {
			if (x == other.x && y == other.y) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "Coordinate: [" + x + "," + y + "]";
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		while (mIsRunning) {

			/** 取得更新游戏之前的时间 **/
			long startTime = System.currentTimeMillis();

			if(mNextMoveDalay <= 0) {
				update();
				mNextMoveDalay = mMoveDelay;
			}

			/** 在这里加上线程安全锁 **/
			synchronized (mSurfaceHolder) {
				/** 拿到当前画布 然后锁定 **/
				Canvas canvas = mSurfaceHolder.lockCanvas();
				draw(canvas);
				/** 绘制结束后解锁显示在屏幕上 **/
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}

			/** 取得更新游戏结束的时间 **/
			long endTime = System.currentTimeMillis();

			/** 计算出游戏一次更新的毫秒数 **/
			int diffTime = (int) (endTime - startTime);

			/** 确保每次更新时间为50帧 **/
			while (diffTime <= TIME_IN_FRAME) {
				diffTime = (int) (System.currentTimeMillis() - startTime);
				/** 线程等待 **/
				Thread.yield();
			}

			mNextMoveDalay -= diffTime;
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mIsRunning = true;
		new Thread(this).start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mIsRunning = false;
	}

}
