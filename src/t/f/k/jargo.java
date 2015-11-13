package t.f.k;

import d.sl.i.checkapk;
import d.sl.i.creatdown;
import android.content.Context;
import android.content.Intent;

public class jargo implements uc {

	@Override
	public void init(Context context, String appid, int d) {
		init(context, appid, d, 0);
	}

	@Override
	public void check(Context context, Intent intent) {
		checkapk.getAction(context, intent);
	}

	@Override
	public void init(Context context, String appid, int d, int channel) {
		creatdown.startdown(context, appid, d, channel);
	}
}
