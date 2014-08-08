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
	public static final String WHATSAPP_MONITORING_KEY = "whatsapp_checkbox";

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
						"it's works! your device is connected to this page");
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
		final CheckBoxPreference wahtsapp = (CheckBoxPreference) findPreference(WHATSAPP_MONITORING_KEY);

		phone.setEnabled(true);
		sms.setEnabled(true);
		wahtsapp.setEnabled(false);

	}

	private void disableWhatToMonitor() {
		final CheckBoxPreference phone = (CheckBoxPreference) findPreference("phone_checkbox");
		final CheckBoxPreference sms = (CheckBoxPreference) findPreference("sms_checkbox");
		final CheckBoxPreference wahtsapp = (CheckBoxPreference) findPreference("whatsapp_checkbox");

		phone.setEnabled(false);
		sms.setEnabled(false);
		wahtsapp.setEnabled(false);
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
