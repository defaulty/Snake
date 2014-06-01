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

package com.sinaapp.thesnake;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.sinaapp.thesnake.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 * 
 */
public class MainActivity extends Activity {
    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the SnakeView.
     * 
     */
    private static Boolean isExit = false;  
    private static Boolean hasTask = false;  
    Timer tExit = new Timer();  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            isExit = false;  
            hasTask = true;  
        }
    };

    private static final String APP_ID = "wxdba8dc1f454d78f7";
    private IWXAPI apiIwxapi;

    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        System.out.println("TabHost_Index.java onKeyDown");  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if(isExit == false ) {
                isExit = true;
                Toast.makeText(this, getResources().getString(R.string.quit_tips), Toast.LENGTH_SHORT).show();
                if(!hasTask) {
                    tExit.schedule(task, 2000);
                }
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.snake_start_layout);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        apiIwxapi = WXAPIFactory.createWXAPI(this, APP_ID, true);
    }

    public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);

//    	mSnakeView = (SnakeView) findViewById(R.id.snake);
//        if(mSnakeView == null) {
//        	Log.i("zzz", "snakeview is null!!!");
//        }
//        mSnakeView.setMode(SnakeView.RUNNING);
//        mSnakeView.setTextView((TextView) findViewById(R.id.text));

//        if (savedInstanceState == null) {
//            // We were just launched -- set up a new game
//            mSnakeView.setMode(SnakeView.READY);
//        } else {
//            // We are being restored
//            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
//            if (map != null) {
//                mSnakeView.restoreState(map);
//            } else {
//                mSnakeView.setMode(SnakeView.PAUSE);
//            }
//        }
    }

    public void startTutorial(View view) {
		Intent intent = new Intent(this, TutorialActivity.class);
		startActivity(intent);
    }

    public void startSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
    }

    public void sendToWX(View view) {
    	WXTextObject object = new WXTextObject();
    	object.text = "Hello WXChat !";
    	
    	WXMediaMessage msg = new WXMediaMessage();
    	msg.mediaObject = object;
    	msg.description = "hello wechat!";
    	
    	SendMessageToWX.Req req = new SendMessageToWX.Req();
    	req.transaction = String.valueOf(System.currentTimeMillis());
    	req.message = msg;

    	apiIwxapi.sendReq(req);
    }
}
