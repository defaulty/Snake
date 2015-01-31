package com.sinaapp.thesnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sinaapp.thesnake.R;
import com.tendcloud.tenddata.TCAgent;

public class LoseActivity extends Activity implements OnClickListener {
	private int mScore = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	String BEST_SCORE = getResources().getText(R.string.snakeview_bestscore).toString();

        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snake_lose_layout);

        Intent intent = getIntent();
        String finalScore = intent.getStringExtra(GameView.FINAL_SCORE);

        int bestScore = 0;
        SharedPreferences bestScorePref = getSharedPreferences(BEST_SCORE, 0);
        if(bestScorePref != null) {
        	bestScore = bestScorePref.getInt(BEST_SCORE, 0);
        }
        mScore = Integer.parseInt(finalScore);
        if(mScore > bestScore) {
        	bestScore = mScore;
        	SharedPreferences.Editor editor = bestScorePref.edit();
            editor.putInt(BEST_SCORE, bestScore);
            editor.commit();
        }

        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(getResources().getText(R.string.snakeview_finalscore) + finalScore + "\n\n" + BEST_SCORE + bestScore);

        ImageButton cancel = (ImageButton) findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        ImageButton restart = (ImageButton)findViewById(R.id.btn_restart);
        restart.setOnClickListener(this);
        ImageButton buttonShareWeixin = (ImageButton)findViewById(R.id.btn_weixin);
        buttonShareWeixin.setOnClickListener(this);
        ImageButton buttonSharePengyouquan = (ImageButton)findViewById(R.id.btn_pengyouquan);
        buttonSharePengyouquan.setOnClickListener(this);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Intent intent = new Intent(this, MainActivity.class);
    		startActivity(intent);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
    	Intent intent;
    	switch(v.getId()) {
    	case R.id.btn_restart:
    		intent = new Intent(this, GameActivity.class);
    		startActivity(intent);
    		break;
    	case R.id.btn_cancel:
    		intent = new Intent(this, MainActivity.class);
    		startActivity(intent);
    		break;
    	case R.id.btn_weixin:
    		if(MainActivity.mWeixinUtil != null) {
    			MainActivity.mWeixinUtil.sendToWX(getResources(), false, mScore);
    		}
    		break;
    	case R.id.btn_pengyouquan:
    		if(MainActivity.mWeixinUtil != null) {
    			MainActivity.mWeixinUtil.sendToWX(getResources(), true, mScore);
    		}
    		break;
    	}
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TCAgent.onResume(this);
	}
}
