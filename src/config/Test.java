package config;

public class Test {
	public static void main(String[] args) {
		String str2 = "//android.widget.FrameLayout[@index='0']/android.widget.LinearLayout[@index='0']";
		String str = "//android.widget.FrameLayout[@index='0']/android.widget.LinearLayout[@index='0']/android.widget.FrameLayout[@index='0']/android.widget.RelativeLayout[@index='0']/android.widget.RelativeLayout[@index='0']/android.widget.LinearLayout[@index='0']/android.widget.TextView[@index='2']";
		System.out.println(str.substring(str2.length()));
	}
}
