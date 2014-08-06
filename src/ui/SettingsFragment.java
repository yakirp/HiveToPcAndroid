package ui;

import com.example.hivetopc.R;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	private static final String CHANNEL_KEY = "channel";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

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
				
				publishEvent(newValue.toString(), "it's works! your device is connected to this page");
				return false;
			}
		});

	}
	
	public void  publishEvent(String channel, String event) {
		System.err.println("44455555555555555555555555555555555555");
		Pubnub pubnub = new Pubnub("demo", "demo");
		
	 
		
		
		 pubnub.publish(channel, event	, new Callback() {
  
			@Override
			public void connectCallback(String arg0, Object arg1) {
				System.err.println("publish to: "+arg1.toString());
				super.connectCallback(arg0, arg1);
			}

			@Override
			public void successCallback(String arg0, Object arg1, String arg2) {
				System.err.println("publish to: "+arg0);
				super.successCallback(arg0, arg1, arg2);
			}
		});
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		System.err.println(key);

		if (key.equalsIgnoreCase(CHANNEL_KEY)) {

		}

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
