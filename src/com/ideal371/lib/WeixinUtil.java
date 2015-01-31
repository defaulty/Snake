package com.ideal371.lib;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

import com.sinaapp.thesnake.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;

public class WeixinUtil {
	private static final String WEIXIN_APP_ID = "wxdba8dc1f454d78f7";
	private static IWXAPI apiIwxapi;

	public WeixinUtil(Context context) {
		apiIwxapi = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID, true);
		apiIwxapi.registerApp(WEIXIN_APP_ID);
	}

	public void unRegister() {
		apiIwxapi.unregisterApp();
	}

	public void sendToWX(Resources res, boolean scene, int score) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://thesnake.sinaapp.com/";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = res.getString(R.string.weixinutil_page_title);
		if(score < 0) {
			msg.description = res.getString(R.string.weixinutil_page_description);
		} else {
			msg.description = res.getString(R.string.weixinutil_page_body_prefix) + score + res.getString(R.string.weixinutil_page_body_suffix);
		}
		Bitmap thumb = BitmapFactory.decodeResource(res, R.drawable.p_1);
		msg.thumbData = bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = scene ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		apiIwxapi.sendReq(req);
	}

	private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
}
