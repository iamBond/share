package com.readboy.multshare;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class MainActivity extends Activity {
	
	//寰俊appid
	private final static String APP_ID = "wx5c734d1f6b8e6db3";
	
	
	//绗笁鏂筧pp鍜屽井淇￠�淇＄殑鎺ュ彛
	private IWXAPI api = null;
	
	private View viewLayout = null;//鐢ㄤ簬寮曠敤瀵硅瘽妗嗗竷	
	private PopupWindow popWin = null; //瀹炵幇瀵硅瘽	
	private ImageButton buttonWeibo = null;
	private ImageButton buttonQQ = null;
	private ImageButton buttonQQZone = null;
	private ImageButton buttonWeichat = null;
	
	private SHARE_MEDIA platform = null;

	/**
	 * 
	 */
	View mShareButton;

	/**
	 * 
	 */
	UMSocialService mController = UMServiceFactory
			.getUMSocialService("myshare");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCustomUI(true,v);
			}
		});
		initSocialSDK();
		initPopupWindow();//鍒濆鍖栧璇濇
	}
	
	/**
	 * 鍒濆鍖朣DK锛屾坊鍔犱竴浜涘钩锟�	 */
	/*
	 * 鍒濆鍖朣DK锛屾坊鍔犱竴浜涘垎鏋愬钩鍙�
	 */
	private void initSocialSDK() {
		// 娣诲姞QQ骞冲彴
		//娣诲姞qq骞冲彴
		UMQQSsoHandler qqHandler = new UMQQSsoHandler(MainActivity.this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qqHandler.addToSocialSDK();

		// 娣诲姞QQ绌洪棿骞冲彴
		//娣诲姞QQ绌洪棿骞冲彴
		QZoneSsoHandler qzoneHandler = new QZoneSsoHandler(MainActivity.this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qzoneHandler.addToSocialSDK();

		

		// 璁剧疆鏂囧瓧鍒嗕韩鍐呭
		//璁剧疆鏂囧瓧鍒嗘瀽鍐呭
		mController.setShareContent("杩欐槸鏂囧瓧鍒嗕韩鍐呭");
		// 鍥剧墖鍒嗕韩鍐呭
		//鍥剧墖鍒嗘瀽鍐呭
		mController.setShareMedia(new UMImage(MainActivity.this,
				R.drawable.umeng_socialize_qq_on));

	}
	
	/**
	 * 鏄剧ず鎮ㄧ殑鑷畾涔夌晫闈紝褰撶敤鎴风偣鍑讳竴涓钩鍙版椂锛岀洿鎺ヨ皟鐢╠irectShare鎴栵拷?postShare鏉ュ垎锟�
	 */
	private void showCustomUI(final boolean isDirectShare,View v) {
		//鎴戠殑淇敼
	
		popWin.showAsDropDown(v.findViewById(R.id.button),-100,0);
		buttonWeibo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				platform = SHARE_MEDIA.SINA;
				mController.postShare(MainActivity.this, platform,
						mShareListener);
			}
		});
		
		buttonQQ.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				platform = SHARE_MEDIA.QQ;
				mController.postShare(MainActivity.this, platform, mShareListener);
			}
		});
		
		buttonQQZone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				platform = SHARE_MEDIA.QZONE;
				mController.postShare(MainActivity.this,platform, mShareListener);
			}
		});
		
		buttonWeichat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				regToWx();
				sendToWx();
			}
		});
		
	}

	/**
	 * 鍒嗕韩鐩戝惉锟�	 */
	SnsPostListener mShareListener = new SnsPostListener() {

		@Override
		public void onStart() {

		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int stCode,
				SocializeEntity entity) {
			if (stCode == 200) {
				Toast.makeText(MainActivity.this, "鍒嗕韩鎴愬姛", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this,
						"鍒嗕韩澶辫触 : error code : " + stCode, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				resultCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * 鎴戣嚜宸辨坊鍔犵殑鍐呭锛屾敼鍙樺脊绐楅锟�	 */
		private void initPopupWindow(){
			
			viewLayout = this.getLayoutInflater().inflate(R.layout.dialog, null);//鑾峰彇瀵硅瘽妗嗗竷锟�iew
			
			buttonWeibo = (ImageButton)viewLayout.findViewById(R.id.button_weibo);
			buttonQQ = (ImageButton)viewLayout.findViewById(R.id.button_qq);
			buttonQQZone = (ImageButton)viewLayout.findViewById(R.id.button_qqzone);
			buttonWeichat = (ImageButton)viewLayout.findViewById(R.id.button_weichat);
			
			
		
		
		popWin = new PopupWindow(viewLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popWin.setBackgroundDrawable(getResources().getDrawable(R.drawable.sword));
	//	popWin.setBackgroundDrawable(getResources().getColor(R.color.umeng_socialize_color_group));
		popWin.setOutsideTouchable(true);
		
	
		
		
		
	}




	
	//娉ㄥ唽鍒板井淇�
	protected void regToWx() {
		//閫氳繃WXAPIFactory宸ュ巶锛岃幏鍙朓WXAPI瀹炰緥
		api = WXAPIFactory.createWXAPI(this, APP_ID, true);
		api.registerApp(APP_ID);
	}

	protected void sendToWx(){
		//鍒濆鍖栦竴涓猈XTextObject瀵硅薄
		WXTextObject textObj = new WXTextObject();
		textObj.text = "textObj";
		
		//鐢╓XTextObjext瀵硅薄鍒濆鍖栦竴涓猈XMediaMessage瀵硅薄
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description ="来自读书郎平板的分享";
		
		//鏋勯�涓�釜Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		
		//璋冪敤api鎺ュ彛鍙戦�鏁版嵁鍒板井淇�
		api.sendReq(req);
	}
	
	
}
