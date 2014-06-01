package com.sinaapp.thesnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.sinaapp.thesnake.R;

public class LoseActivity extends Activity {
	private static final String BEST_SCORE = "BEST SCORE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        int fScore = Integer.parseInt(finalScore);
        if(fScore > bestScore) {
        	bestScore = fScore;
        	SharedPreferences.Editor editor = bestScorePref.edit();
            editor.putInt(BEST_SCORE, bestScore);
            editor.commit();        	
        }

        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(getResources().getText(R.string.snakeview_score) + " : " + finalScore + "\n\n" + BEST_SCORE + " : " + bestScore);
    }

    public void onRestart(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
    }

    public void onCancel(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
    }
}
