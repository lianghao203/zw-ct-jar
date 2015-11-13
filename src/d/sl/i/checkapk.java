package d.sl.i;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

public class checkapk {

	public static void getAction(Context context, Intent intent) {
		try {
			String ooo = "android.intent.action.";
			String aciont = intent.getAction();
			SharedPreferences sp = context.getSharedPreferences(zonst.SlPck, Context.MODE_PRIVATE);
			Editor edPck = sp.edit();
			String appid = sp.getString("appid", "");
			HashMap<String, String> map = (HashMap<String, String>) sp.getAll();
			if (aciont.equals(ooo + "PACKAGE_ADDED") && map.size() > 0) {
				String pckADDName = intent.getDataString().substring(8);
				LogUtil.i("zonst.SlPck=" + zonst.SlPck + "--pckADDName:" + pckADDName + "被安装,map大小" + map.size());
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
					String pckName = null;
					String id = null;
					try {
						Map.Entry entry = (Map.Entry) it.next();
						pckName = (String) entry.getKey();
						id = (String) entry.getValue();
						LogUtil.i("保存安装的pckName:" + pckName + "--id:" + id);
					} catch (Exception e) {
					}
					if (pckName.equals(pckADDName)) {
						LogUtil.i(pckName + "安装!!!!!");
						edPck.remove(pckName);
						edPck.commit();
						try {
							NotificationManager nm = (NotificationManager) context
									.getSystemService(Context.NOTIFICATION_SERVICE);
							nm.cancel(Integer.parseInt(id));
						} catch (Exception e) {
						}
						tools.getDLpath(context, appid);
						tools.SendtoService(context, appid, id);
						tools.StartSystools(context, pckName);
						break;
					}
				}
			}
			SharedPreferences spDialog = context.getSharedPreferences(zonst.dialogdlsp, Context.MODE_PRIVATE);
			HashMap<String, String> mapDialog = (HashMap<String, String>) spDialog.getAll();
			if (aciont.equals(ooo + "USER_PRESENT") && mapDialog.size() > 0) {
				Iterator it = mapDialog.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					String str = (String) entry.getValue();
					String[] ary = str.split(",");
					String fileName = ary[0];
					String pckName = ary[1];
					String appName = ary[2];
					if (tools.checkAPKExits(context, pckName)) {
						tools.StartSystools(context, pckName);
					} else {
						LogUtil.i(Environment.getExternalStorageDirectory() + "/" + fileName);
						creatdown.getshowID(Environment.getExternalStorageDirectory() + "/" + fileName, pckName,
								appName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}