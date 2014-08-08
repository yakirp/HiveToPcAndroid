package utils;

import services.MonitoringService;
import ui.SettingsFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

}
