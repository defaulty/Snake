package com.ideal371.lib;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class IdealImageButton extends ImageButton {
	public final static float[] BT_SELECTED = new float[] { 2, 0, 0, 0, 2, 0,
			2, 0, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 1, 0 };

	public final static float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	public IdealImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setListener();
	}

	public IdealImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListener();
	}

	public IdealImageButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setListener();
	}

	// public IdealImageButton(Context context, AttributeSet attrs, int
	// defStyleAttr, int defStyleRes) {
	// super(context, attrs, defStyleAttr, defStyleRes);
	// setListener();
	// }

	private void setListener() {
		Log.i("Snake", "set listener");
		setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("Snake", "onTouch");
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				}
				return false;
			}
		});
		setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i("Snake", "onFocusChange");
				if (hasFocus) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				}
			}
		});
	}
}
