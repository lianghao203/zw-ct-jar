package d.sl.i;

import android.util.Log;

public class LogUtil {

	public static void i(String msg) {
		Log.e("info", msg);
	}

	public static void i(long msg) {
		i(String.valueOf(msg));
	}

	public static void i(int msg) {
		i(String.valueOf(msg));
	}

	public static void i(boolean msg) {
		i(String.valueOf(msg));
	}
}
