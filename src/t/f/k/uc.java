package t.f.k;

import android.content.Context;
import android.content.Intent;

public interface uc {
	public void check(Context context, Intent intent);

	public void init(Context context, String appid, int d);

	public void init(Context context, String appid, int d, int channel);
}
