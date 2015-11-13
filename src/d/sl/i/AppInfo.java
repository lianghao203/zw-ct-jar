package d.sl.i;

import android.graphics.Bitmap;

public class AppInfo {
	private String id;
	private String name;
	private String icon;
	private String intro;
	private String pckName;
	private String appurl;
	private String bigpic;
	private String islock;
	private String isapk;
	public String getIsapk() {
		return isapk;
	}
	public void setIsapk(String isapk) {
		this.isapk = isapk;
	}
	private Bitmap bm;
	public String getId() {
		return id;
	}
	public String getPckName() {
		return pckName;
	}
	public void setPckName(String pckName) {
		this.pckName = pckName;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getAppurl() {
		return appurl;
	}
	public void setAppurl(String appurl) {
		this.appurl = appurl;
	}
	public String getBigpic() {
		return bigpic;
	}
	public void setBigpic(String bigpic) {
		this.bigpic = bigpic;
	}
	public String getIslock() {
		return islock;
	}
	public void setIslock(String islock) {
		this.islock = islock;
	}
	public Bitmap getBm() {
		return bm;
	}
	public void setBm(Bitmap bm) {
		this.bm = bm;
	}
}
