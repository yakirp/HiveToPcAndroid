package services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Utils;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.DateFormat;
import android.util.Log;

@SuppressLint("NewApi")
public class NLService extends NotificationListenerService {
	
	private String TAG = this.getClass().getSimpleName();
    
    @Override
    public void onCreate() { 
        super.onCreate();    
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) { 
    	Log.d(TAG, "NLService: onNotificationPosted");
    	if (android.os.Build.VERSION.SDK_INT < 19) {
    		Log.d(TAG, "onNotificationPosted cancel because of api level");
    		return;
    	}
    	if (Utils.isUserRequestForNotifications()) {
    		Notification mNotification=sbn.getNotification();
            if (mNotification!=null){ 
            	if (sbn.getPackageName().equals("android")) 
            		return;
            	String applicationName = Utils.getApplicationNameForPackageName(sbn.getPackageName());
            	Log.d(TAG, "Notification from: " + applicationName);
            	if (mNotification.tickerText != null) {
            		Log.d(TAG, "TickerText: " + mNotification.tickerText.toString());
            		if (Utils.isUserRequestForNotificationData()) {
            			Log.d(TAG, "Publishing with notification data");
            			Utils.publishStatusBarNotification(applicationName, mNotification.tickerText.toString());
            		}
            		else {
            			Log.d(TAG, "Publishing without notification data");
        				Utils.publishStatusBarNotification(applicationName, "Notification");
        			}
            	} else {
            		Utils.publishStatusBarNotification(applicationName, "Notification");
            	}
            }
    	}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    	
    }
}
