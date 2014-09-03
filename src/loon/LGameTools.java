package loon;

import java.io.IOException;
import java.util.Hashtable;

import loon.LGame.Location;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInput.ClickEvent;
import loon.core.input.LInput.SelectEvent;
import loon.core.input.LInput.TextEvent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;


public class LGameTools {

	final static class ClickAndroid implements
			android.content.DialogInterface.OnClickListener,
			android.content.DialogInterface.OnCancelListener {

		private int selectFlag;

		private Object event;

		private android.widget.EditText edit;

		public ClickAndroid(Object e, int f) {
			this.event = e;
			this.selectFlag = f;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			if (event == null) {
				return;
			}
			if (event instanceof ClickEvent) {
				switch (selectFlag) {
				case 0:
					((ClickEvent) event).clicked();
					break;
				case 1:
					((ClickEvent) event).cancel();
					break;
				}
			} else if (event instanceof SelectEvent) {
				switch (selectFlag) {
				case 0:
					((SelectEvent) event).item(whichButton);
					break;
				case 1:
					((SelectEvent) event).cancel();
					break;
				}
			} else if (event instanceof TextEvent) {
				switch (selectFlag) {
				case 0:
					((TextEvent) event).input(edit.getText().toString());
					break;
				case 1:
					((TextEvent) event).cancel();
					break;
				}
			}
		}

		public void setInput(android.widget.EditText e) {
			if (event instanceof TextEvent) {
				this.edit = e;
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if (event instanceof ClickEvent) {
				switch (selectFlag) {
				case 1:
					((ClickEvent) event).cancel();
					break;
				}
			} else if (event instanceof SelectEvent) {
				switch (selectFlag) {
				case 1:
					((SelectEvent) event).cancel();
					break;
				}
			} else if (event instanceof TextEvent) {
				switch (selectFlag) {
				case 1:
					((TextEvent) event).cancel();
					break;
				}
			}
		}

	}

	final static class Logo implements LRelease {

		private int centerX, centerY;

		private float alpha = 0f;

		private float curFrame, curTime;

		boolean finish, inToOut;

		LTexture logo;

		public Logo(LTexture texture) {
			this.logo = texture;
			this.curTime = 60;
			this.curFrame = 0;
			this.inToOut = true;
		}

		public void draw(final GLEx gl) {
			if (logo == null || finish) {
				return;
			}
			if (!logo.isLoaded()) {
				this.logo.loadTexture();
			}
			if (centerX == 0 || centerY == 0) {
				this.centerX = (int) (LSystem.screenRect.width * LSystem.scaleWidth)
						/ 2 - logo.getWidth() / 2;
				this.centerY = (int) (LSystem.screenRect.height * LSystem.scaleHeight)
						/ 2 - logo.getHeight() / 2;
			}
			if (logo == null || !logo.isLoaded()) {
				return;
			}
			alpha = (curFrame / curTime);
			if (inToOut) {
				curFrame++;
				if (curFrame == curTime) {
					alpha = 1f;
					inToOut = false;
				}
			} else if (!inToOut) {
				curFrame--;
				if (curFrame == 0) {
					alpha = 0f;
					finish = true;
				}
			}
			gl.reset(true);
			gl.setAlpha(alpha);
			gl.drawTexture(logo, centerX, centerY);
		}

		@Override
		public void dispose() {
			if (logo != null) {
				logo.destroy();
				logo = null;
			}
		}
	}

	final static class Web extends android.webkit.WebView {

		
		public interface JavaScript {

			// 执行的对象，实际内部应该封装具体类
			public Object getObject();

			// 脚本类名
			public String getName();

		}

		
		public interface WebProcess {

			// 页面开始加载
			public void onPageStarted(String url, Bitmap favicon);

			// 页面加载完成
			public void onPageFinished(String url);

			// 资源载入
			public void onLoadResource(String url);

			// 即将重载链接前
			public void shouldOverrideUrlLoading(String url);

			// 接受请求
			public void onReceivedHttpAuthRequest(
					android.webkit.HttpAuthHandler handler, String host,
					String realm);

		}

		private LGame activity;

		private android.webkit.WebSettings webSettings;

		private String url;

		public Web(String url) {
			this(LSystem.screenActivity, null, url);
		}

		public Web(String url, WebProcess webProcess) {
			this(LSystem.screenActivity, webProcess, url);
		}

		public Web(LGame activity, String url) {
			this(activity, null, url);
		}

		public Web(final LGame activity, final WebProcess webProcess,
				final String url) {

			super(activity);
			this.url = url;
			this.activity = activity;
			// 允许显示滚动条
			this.setHorizontalScrollBarEnabled(true);
			// 清空原有的缓存数据
			this.clearCache(true);
			// 隐藏当前View
			this.setVisible(false);
			// 不要背景图
			java.lang.reflect.Method drawable = null;
			try {
				drawable = this.getClass().getMethod("setBackgroundDrawable",
						android.graphics.drawable.Drawable.class);
				drawable.invoke(this, (android.graphics.drawable.Drawable) null);
			} catch (Exception ex) {
				try {
					drawable = this.getClass().getMethod("setBackground",
							android.graphics.drawable.Drawable.class);
					drawable.invoke(this,
							(android.graphics.drawable.Drawable) null);
				} catch (Exception e) {

				}
			}
			// 进行细节设置
			webSettings = getSettings();
			// 数据库访问权限开启
			webSettings.setAllowFileAccess(true);
			// 密码保存与Form信息不保存
			webSettings.setSavePassword(false);
			webSettings.setSaveFormData(false);
			if (!webSettings.getJavaScriptEnabled()) {
				// 响应JavaScript事件
				webSettings.setJavaScriptEnabled(true);
			}
			// 允许JavaScript脚本打开新的窗口
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
			// 允许自动加载图像资源
			webSettings.setLoadsImagesAutomatically(true);
			// 不支持网页缩放
			webSettings.setSupportZoom(false);

			// 当流程监听存在时
			if (webProcess != null) {
				setWebViewClient(new android.webkit.WebViewClient() {

					@Override
					public void onPageStarted(android.webkit.WebView view,
							String url, Bitmap favicon) {
						webProcess.onPageStarted(url, favicon);
						super.onPageStarted(view, url, favicon);
					}

					@Override
					public void onPageFinished(android.webkit.WebView view,
							String url) {
						webProcess.onPageFinished(url);
						super.onPageFinished(view, url);
					}

					@Override
					public void onLoadResource(android.webkit.WebView view,
							String url) {
						webProcess.onLoadResource(url);
						super.onLoadResource(view, url);
					}

					@Override
					public boolean shouldOverrideUrlLoading(
							android.webkit.WebView view, String url) {
						webProcess.shouldOverrideUrlLoading(url);
						return super.shouldOverrideUrlLoading(view, url);
					}

					@Override
					public void onReceivedHttpAuthRequest(
							android.webkit.WebView view,
							android.webkit.HttpAuthHandler handler,
							String host, String realm) {
						webProcess.onReceivedHttpAuthRequest(handler, host,
								realm);
						super.onReceivedHttpAuthRequest(view, handler, host,
								realm);
					}

				});
			}

			// 加载进度条
			final android.widget.ProgressBar progress = new android.widget.ProgressBar(
					activity);
			activity.addView(progress, Location.CENTER);
			setWebChromeClient(new android.webkit.WebChromeClient() {
				@Override
				public void onProgressChanged(
						final android.webkit.WebView view, final int newProgress) {
					LSystem.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progress.setProgress(newProgress);
							progress.setVisibility(newProgress == 100 ? View.GONE
									: View.VISIBLE);
							if (newProgress == 100) {
								activity.removeView(progress);
							}
							setVisible(newProgress == 100 ? true : false);
						}
					});

				}
			});
			if (url != null) {
				loadUrl(url);
			}
		}

		
		@SuppressWarnings("unchecked")
		public void setWebParams(String name, Object value) {
			Hashtable<String, Object> params = null;
			if (getTag() == null) {
				params = new Hashtable<String, Object>();
				setTag(params);
			} else {
				params = (Hashtable<String, Object>) getTag();
			}
			params.put(name, value);
		}

		
		@SuppressWarnings("unchecked")
		public Object getWebParams(String name) {
			if (getTag() == null) {
				setTag(new Hashtable<String, Object>());
			}
			Hashtable<String, Object> params = (Hashtable<String, Object>) getTag();
			return params.get(name);
		}

		
		public void addJavaScripts(JavaScript[] sprites) {
			for (int i = 0; i < sprites.length; i++) {
				this.addJavascriptInterface(sprites[i].getObject(),
						sprites[i].getName());
			}
		}

		
		public void addJavaScripts(JavaScript sprite) {
			this.addJavascriptInterface(sprite.getObject(), sprite.getName());
		}

		public void setVisible(boolean isVisible) {
			if (isVisible) {
				this.setVisibility(View.VISIBLE);
			} else {
				this.setVisibility(View.GONE);
			}
		}

		public void callScriptFunction(String function) {
			super.loadUrl("javascript:" + function);
		}

		
		public void loadIntent(Intent intent) {
			this.loadUrl(intent.getStringExtra(android.app.SearchManager.QUERY));
		}

		
		@Override
		public void loadUrl(String url) {
			boolean isURL = url.startsWith("http://")
					|| url.startsWith("https://") || url.startsWith("ftp://");
			if (!isURL) {
				try {
					if (activity.getAssets().open(url) != null) {
						super.loadUrl("file:///android_asset/" + url);
					} else {
						super.loadUrl("http://" + url);
					}
				} catch (IOException e) {
					super.loadUrl("http://" + url);
				}
			} else {
				super.loadUrl(url);
			}
		}

		public void loadData(String data) {
			loadData(data, "text/html", LSystem.encoding);
		}

		public void loadData(String data, String encoding) {
			super.loadData(data, "text/html", encoding);
		}

		public android.webkit.WebSettings getWebSettings() {
			return webSettings;
		}

		public String getURL() {
			return url;
		}

	}

}
