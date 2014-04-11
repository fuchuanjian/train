package com.chuanonly.notes;

import java.lang.ref.WeakReference;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class MainActivity extends LGame {
	private AdView mAdView;
	private static WeakReference<Handler> mHandlerRef;
	@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR=true;
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.showFPS = false;
		setting.landscape = true;
		setting.fps = 30;
		register(setting, GameMain.class);
		if (Util.isNetworkAvailable(APP.getContext()))
		{
			loadAd();			
		}
		_bottomLayout.bringToFront();
		_bottomLayout.setVisibility(View.GONE);
		mHandlerRef = new WeakReference<Handler>(mHandler);
	}
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if (msg.what == 0)
			{
				if (mAdView == null)
				{
					loadAd();
				}
				_bottomLayout.setVisibility(View.VISIBLE);				
			}else if (msg.what == 1)
			{
				_bottomLayout.setVisibility(View.GONE);
			}
		};
	};
	public static void showAd()
	{
		if (Util.isNetworkAvailable(APP.getContext()))
		{			
			Handler h =  mHandlerRef.get();
			if (h != null)
			{
				h.sendEmptyMessage(0);
			}
		}
	}
	public static void hideAd()
	{
		if (Util.isNetworkAvailable(APP.getContext()))
		{			
			Handler h =  mHandlerRef.get();
			if (h != null)
			{
				h.sendEmptyMessage(1);
			}
		}
	}
	private void loadAd()
	{
		mAdView = new AdView(this, AdSize.BANNER, "a1530385879fa18");
		_bottomLayout.addView(mAdView);
		mAdView.loadAd(new AdRequest());	
	}
}
