package ui;

import utils.PubNubHelper;
import utils.Utils;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.hivetopc.R;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	public static final String START_MONITORING_KEY = "start_monitoring";
	public static final String CHANNEL_KEY = "channel";
	public static final String PHONE_CALL_MONITORING_KEY = "phone_checkbox";
	public static final String SMS_MONITORING_KEY = "sms_checkbox";
	public static final String NOTIFICATIONS_MONITORING_KEY = "notifications_checkbox";
	public static final String NOTIFICATION_DATA_MONITORING_KEY = "notification_data_checkbox";
	public static final String BATTERY_MONITORING_KEY = "battery_checkbox";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
  
		initChannelEditText();
		
		if (getPreferenceScreen().getSharedPreferences().getBoolean(
				START_MONITORING_KEY, true)) {
			Utils.startMonitoringService();
			disableWhatToMonitor();
		} else {
			Utils.stopMonitoringService();
			enableWhatToMonitor();
		}
	}

	private void initChannelEditText() {
		final EditTextPreference etp = (EditTextPreference) findPreference(CHANNEL_KEY);

		etp.setTitle(getPreferenceScreen().getSharedPreferences().getString(
				CHANNEL_KEY, "Tap to add channel"));

		etp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				etp.setTitle(newValue.toString());
				getPreferenceScreen().getSharedPreferences().edit()
						.putString(CHANNEL_KEY, newValue.toString()).commit();

				PubNubHelper.getInstance().publish(newValue.toString(),
						"Your device is connected to this page");
				return false;
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equalsIgnoreCase(CHANNEL_KEY)) {

		}

		if (key.equalsIgnoreCase(START_MONITORING_KEY)) {

			if (getPreferenceScreen().getSharedPreferences().getBoolean(
					START_MONITORING_KEY, true)) {
				Utils.startMonitoringService();
				disableWhatToMonitor();
			} else {
				Utils.stopMonitoringService();
				enableWhatToMonitor();
			}

		}

	}

	private void enableWhatToMonitor() {
		final CheckBoxPreference phone = (CheckBoxPreference) findPreference(PHONE_CALL_MONITORING_KEY);
		final CheckBoxPreference sms = (CheckBoxPreference) findPreference(SMS_MONITORING_KEY);
		final CheckBoxPreference notifications = (CheckBoxPreference) findPreference(NOTIFICATIONS_MONITORING_KEY);
		final CheckBoxPreference notificationData = (CheckBoxPreference) findPreference(NOTIFICATION_DATA_MONITORING_KEY);
		final CheckBoxPreference battery = (CheckBoxPreference) findPreference(BATTERY_MONITORING_KEY);

		phone.setEnabled(true);
		sms.setEnabled(true);
		notifications.setEnabled(true);
		notificationData.setEnabled(true);
		battery.setEnabled(true);

	}

	private void disableWhatToMonitor() {
		final CheckBoxPreference phone = (CheckBoxPreference) findPreference("phone_checkbox");
		final CheckBoxPreference sms = (CheckBoxPreference) findPreference("sms_checkbox");
		final CheckBoxPreference notifications = (CheckBoxPreference) findPreference("notifications_checkbox");
		final CheckBoxPreference notificationData = (CheckBoxPreference) findPreference(NOTIFICATION_DATA_MONITORING_KEY);
		final CheckBoxPreference battery = (CheckBoxPreference) findPreference(BATTERY_MONITORING_KEY);

		phone.setEnabled(false);
		sms.setEnabled(false);
		notifications.setEnabled(false);
		notificationData.setEnabled(false);
		battery.setEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
}
