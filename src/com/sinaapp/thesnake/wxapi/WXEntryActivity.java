package com.sinaapp.thesnake.wxapi;

import com.sinaapp.thesnake.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
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

}
