package utils;

import services.MonitoringService;
import ui.SettingsFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class Utils {

	public static void publishEvent(String event,
			boolean publishOnlyIfMonitoring) {
		if (publishOnlyIfMonitoring) {
			if (isMonitoring()) {
				if (!getCurrentChannel().equalsIgnoreCase("")) {
					PubNubHelper.getInstance().publish(getCurrentChannel(), 
							event);
				}
			}
		} else {
			PubNubHelper.getInstance().publish(getCurrentChannel(),
					event);
		}

	}

	public static String getCurrentChannel() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getString(SettingsFragment.CHANNEL_KEY, "");

	}

	public static void startMonitoringService() {
		Intent i = new Intent(Application.getContext(), MonitoringService.class);

		Application.getContext().startService(i);
	}

	public static void stopMonitoringService() {
		Application.getContext().stopService(
				new Intent(Application.getContext(), MonitoringService.class));
	}

	public static boolean isMonitoring() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.START_MONITORING_KEY, false);

	}
	
	public static boolean isNotificationSettingsEnabled() { 
	    ContentResolver contentResolver = Application.getContext().getContentResolver();
	    String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
	    String packageName = Application.getContext().getPackageName();

	    return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
	}
	
	public static String getApplicationNameForPackageName(String packageName) {
		final PackageManager pm = Application.getContext().getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		
		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
		return applicationName;
	}
	
	public static boolean isUserRequestForPhoneMonitoring() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.PHONE_CALL_MONITORING_KEY, false);

	}
	
	public static boolean isUserRequestForSMSMonitoring() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.SMS_MONITORING_KEY, false);
	}
	
	public static boolean isUserRequestForNotifications() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.NOTIFICATIONS_MONITORING_KEY, false);
	}
	
	public static boolean isUserRequestForNotificationData() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.NOTIFICATION_DATA_MONITORING_KEY, false);
	}
	
	public static boolean isUserRequestForBatteryMonitoring() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(Application.getContext());
		return prefs.getBoolean(SettingsFragment.BATTERY_MONITORING_KEY, false);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isScreenOn() {
//		Application.getContext();
//		PowerManager powerManager = (PowerManager) Application.getContext().getSystemService(Context.POWER_SERVICE);
//		if (powerManager.isScreenOn())
//		{ 
//			return true; 
//		}
//		
//		return false;
		return false;
	}

}
