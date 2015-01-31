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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ideal371.lib.WeixinUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tendcloud.tenddata.TCAgent;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 * 
 */
public class MainActivity extends Activity implements OnClickListener, IWXAPIEventHandler {
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

    static WeixinUtil mWeixinUtil = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

        //TalkingData SDK initialize
        com.tendcloud.tenddata.TCAgent.init(this);
        //Let TalkingData collect uncaught exception automatically
        TCAgent.setReportUncaughtExceptions(true);

        setContentView(R.layout.start_layout);

        ImageButton gamePlay = (ImageButton) findViewById(R.id.gamePlay);
		gamePlay.setOnClickListener(this);
        ImageButton buttonGameTutorial = (ImageButton)findViewById(R.id.gameTutorial);
        buttonGameTutorial.setOnClickListener(this);
        ImageButton buttonSettings = (ImageButton)findViewById(R.id.settings);
        buttonSettings.setOnClickListener(this);
        ImageButton buttonShareWeixin = (ImageButton)findViewById(R.id.share_weixin);
        buttonShareWeixin.setOnClickListener(this);
        ImageButton buttonSharePengyouquan = (ImageButton)findViewById(R.id.share_pengyouquan);
        buttonSharePengyouquan.setOnClickListener(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mWeixinUtil = new WeixinUtil(this);
    }

    @Override
    public void onClick(View v) {
    	Intent intent;
    	switch(v.getId()) {
    	case R.id.gamePlay:
    		intent = new Intent(this, GameActivity.class);
    		startActivity(intent);
//    		overridePendingTransition(R.anim.right_in, R.anim.left_out);
    		break;
    	case R.id.gameTutorial:
    		intent = new Intent(this, TutorialActivity.class);
    		startActivity(intent);
//    		overridePendingTransition(R.anim.right_in, R.anim.left_out);
    		break;
    	case R.id.settings:
    		intent = new Intent(this, SettingsActivity.class);
    		startActivity(intent);
    		overridePendingTransition(R.anim.right_in, R.anim.left_out);
    		break;
    	case R.id.share_weixin:
    		if(mWeixinUtil != null) {
    			mWeixinUtil.sendToWX(getResources(), false, -1);
    		}
    		break;
    	case R.id.share_pengyouquan:
    		if(mWeixinUtil != null) {
    			mWeixinUtil.sendToWX(getResources(), true, -1);
    		}
    		break;
    	}
    }
    
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
Log.i("weixinutil", resp.errCode+"");
		// TODO Auto-generated method stub
		int result = 0;

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.weixinutil_errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.weixinutil_errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.weixinutil_errcode_deny;
			break;
		default:
			result = R.string.weixinutil_errcode_unknown;
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWeixinUtil.unRegister();
		mWeixinUtil = null;
	}
}
