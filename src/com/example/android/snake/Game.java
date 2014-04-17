package com.example.android.snake;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Game extends Activity {
	public SnakeView mSnakeView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snake_layout);

//        mSnakeView = (SnakeView) findViewById(R.id.snake);
//        mSnakeView.initNewGame();
//        mSnakeView.setMode(SnakeView.RUNNING);
    }
}
