package com.sinaapp.thesnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.sinaapp.thesnake.R;

public class SnakeLose extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snake_lose_layout);

        Intent intent = getIntent();
        String finalScore = intent.getStringExtra(SnakeView.FINAL_SCORE);

        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(getResources().getText(R.string.snakeview_score) + " : " + finalScore);
    }

    public void onRestart(View view) {
		Intent intent = new Intent(this, Game.class);
		startActivity(intent);
    }

    public void onCancel(View view) {
		Intent intent = new Intent(this, Snake.class);
		startActivity(intent);
    }
}
