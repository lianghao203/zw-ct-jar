package t.f.k;

import d.sl.i.checkapk;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DL extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		checkapk.getAction(context, intent);
	}
}
