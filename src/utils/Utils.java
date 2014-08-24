package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import services.MonitoringService;
import ui.SettingsFragment;
import android.annotation.SuppressLint;
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

	public static void publishStatusBarNotification(String applicationName, String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Application", applicationName);
			jo.put("Type", "StatusBarNotification");
			jo.put("Message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishJsonEvent(jo, false);
	}
	
	public static void publishBatteryNotification(int level, boolean isCharging) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Application", "Battery");
			jo.put("Type", "BatteryNotification");
			jo.put("Level", level);
			jo.put("Charging", isCharging);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishJsonEvent(jo, true);
	}
	
	public static void publishHiveNotification(String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Application", "HiveToPc");
			jo.put("Type", "HiveNotification");
			jo.put("Message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishJsonEvent(jo, false);
	}
	
	public static void publishPhoneNotification(String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Application", "Phone");
			jo.put("Type", "PhoneNotification");
			jo.put("Message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishJsonEvent(jo, true);
	}
	
	public static void publishSMSNotification(String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Application", "SMS");
			jo.put("Type", "SMSNotification");
			jo.put("Message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishJsonEvent(jo, true);
	}
	
	@SuppressLint("SimpleDateFormat")
	private static void publishJsonEvent(org.json.JSONObject json, boolean publishOnlyIfMonitoring)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		try {
			json.put("Time", dateString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishEvent(json, publishOnlyIfMonitoring);
	}
	
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
	
	public static void publishEvent(org.json.JSONObject json,
			boolean publishOnlyIfMonitoring) {
		if (publishOnlyIfMonitoring) {
			if (isMonitoring()) {
				if (!getCurrentChannel().equalsIgnoreCase("")) {
					PubNubHelper.getInstance().publish(getCurrentChannel(), 
							json);
				}
			}
		} else {
			PubNubHelper.getInstance().publish(getCurrentChannel(),
					json);
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
