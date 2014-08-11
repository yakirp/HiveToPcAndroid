package services;

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
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

 
public class MonitoringService extends NotificationListenerService {

	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;
	private SmsListener smsListener = new SmsListener();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		start();

		if (Utils.isUserRequestForPhoneMonitoring()) {
			startPhoneMonitoring();
		}

		if (Utils.isUserRequestForSMSMonitoring()) {
			startSMSMonitoring();
		}

		Utils.publishEvent("Monitoring Start", false);

		return (START_NOT_STICKY);
	}

	private void start() {
		Intent dismissIntent = new Intent(this, HiveToPcActivity.class);
		dismissIntent.setAction("stop");
		PendingIntent piDismiss = PendingIntent.getService(this, 0,
				dismissIntent, 0);

		// Constructs the Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(Constants.NOTIFICATION_TITLE)
				.setContentText("Active channel " + Utils.getCurrentChannel())
				.setDefaults(Notification.DEFAULT_ALL)

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

	private void startPhoneMonitoring() {

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, final String number) {
				String currentPhoneState = null;
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:

					currentPhoneState = "Device is ringing. Call from "
							+ number;

					Utils.publishEvent(
							"Device is ringing. Call from " + number, true);

					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					currentPhoneState = "Device call state is currently Off Hook";
					Utils.publishEvent(currentPhoneState, true);
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					currentPhoneState = "Device call state is currently Idle";

					Utils.publishEvent(currentPhoneState, true);
					break;
				}

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

	@Override
	public void onDestroy() {

		if (Utils.isUserRequestForPhoneMonitoring()) {
			stopPhoneMonitoring();
		}

		if (Utils.isUserRequestForSMSMonitoring()) {
			stopSMSMonitoring();
		}

		Utils.publishEvent("Monitoring stop", false);
		stopForeground(true);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		System.err.println("00000000000000");
		Utils.publishEvent(sbn.getNotification().tickerText + " " + sbn.getPackageName(),true);

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		// TODO Auto-generated method stub

	}

}
