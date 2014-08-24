package services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import receivers.BatteryListener;
import receivers.SmsListener;
import ui.HiveToPcActivity;
import utils.Constants;
import utils.Utils;

import com.hivetopc.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

 
public class MonitoringService extends Service {

	private PhoneStateListener phoneStateListener; 
	private TelephonyManager telephonyManager;
	private SmsListener smsListener = new SmsListener();
	private BatteryListener batteryListener = new BatteryListener();
	MediaRecorder recorder = null;

	private void startRecording(File file) {
		if (recorder != null) {
			recorder.release();
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(AudioSource.MIC);
		recorder.setOutputFormat(OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(AudioEncoder.AMR_WB);
		recorder.setOutputFile(file.getAbsolutePath());
		try {
			recorder.prepare();
			recorder.start();
		} catch (IOException e) {
			Log.e("giftlist",
					"io problems while preparing [" + file.getAbsolutePath()
							+ "]: " + e.getMessage());
		}
	}
	
	private void stopRecording() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

//		startRecording(new File("/dev/null"));
		
		start();

		if (Utils.isUserRequestForPhoneMonitoring()) {  
			startPhoneMonitoring();
		}

		if (Utils.isUserRequestForSMSMonitoring()) {
			startSMSMonitoring();
		}
		
		if (Utils.isUserRequestForBatteryMonitoring()) {
			startBatteryMonitoring();
		}

		Utils.publishHiveNotification("Monitoring Start");

		return (START_NOT_STICKY);
	}

	private void start() {
		Intent dismissIntent = new Intent(this, HiveToPcActivity.class);
		dismissIntent.setAction("stop");
		PendingIntent piDismiss = PendingIntent.getService(this, 0,
				dismissIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
  				this)
  				.setSmallIcon(R.drawable.ic_launcher) 
  				.setContentTitle(Constants.NOTIFICATION_TITLE)
  				.setDefaults(Notification.DEFAULT_LIGHTS)
  				.setOngoing(true)
  				.setStyle(new NotificationCompat.BigTextStyle())
  				.addAction(R.drawable.ic_launcher, "Stop Monitoring", piDismiss);

  		Notification n = builder.build();

  		n.flags |= Notification.FLAG_NO_CLEAR; 

  		startForeground(1337, n);
	}

	private void startSMSMonitoring() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");

		registerReceiver(smsListener, filter);
	}
	
	private void startBatteryMonitoring() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);

		registerReceiver(batteryListener, filter);
	}

	private void startPhoneMonitoring() {

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, final String number) {
				String currentPhoneState = "";
				
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:

					currentPhoneState = "Device is ringing. Call from "
							+ number;
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					currentPhoneState = "Device call state is currently Off Hook";
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					currentPhoneState = "Device call state is currently Idle";
					break;
				}
				Utils.publishPhoneNotification(currentPhoneState);
			}

		};
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void stopPhoneMonitoring() {
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);

	}

	private void stopSMSMonitoring() {
		unregisterReceiver(smsListener);
	}
	
	private void stopBatteryMonitoring() {
		unregisterReceiver(batteryListener);
	}

	@Override
	public void onDestroy() {

		if (Utils.isUserRequestForPhoneMonitoring()) {
			stopPhoneMonitoring();
		}

		if (Utils.isUserRequestForSMSMonitoring()) {
			stopSMSMonitoring();
		}
		
		if (Utils.isUserRequestForBatteryMonitoring()) {
			stopBatteryMonitoring();
		}

		Utils.publishHiveNotification("Monitoring Stop");
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override     
	public boolean onUnbind(Intent mIntent) {       
	    boolean mOnUnbind = super.onUnbind(mIntent);        
	    return mOnUnbind;     
	}
}
