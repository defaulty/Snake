package com.sinaapp.thesnake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.sinaapp.thesnake.R;

public class Game extends Activity {
    private static String ICICLE_KEY = "snake-view";

    public SnakeView mSnakeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.i("zzz", "onCreate");

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.snake_layout);

        mSnakeView = (SnakeView) findViewById(R.id.snake);

	      if (savedInstanceState != null) {
	      	Log.i("zzz", "onCreate, saveInstanceState is not null");
	      // We are being restored
	      Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
	      if (map != null) {
	          mSnakeView.restoreState(map);
	      } else {
	          mSnakeView.setMode(SnakeView.PAUSE);
	      }
	  }

//        mSnakeView.initNewGame();
//        mSnakeView.setMode(SnakeView.RUNNING);
    }

    @Override
    protected void onPause() {
    	Log.i("zzz", "onPause");
        super.onPause();

        mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.i("zzz", "onSaveInstance");
        //Store the game state
//        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }
    
//    @Override
//    protected void onResume() {
//    	
//    }
//    
//    @Override
//    protected void onRestoreInstanceState (Bundle savedInstanceState) {
//    	
//    }
}
