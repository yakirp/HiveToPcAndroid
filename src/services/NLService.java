package services;

import utils.Utils;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

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
    	if (Utils.isUserRequestForWhatsapp()) {
    		Notification mNotification=sbn.getNotification();
            if (mNotification!=null){
            	if (sbn.getPackageName().contains("com.whatsapp")) {
            		Log.d("Hive", "Whatsapp notification");
            		if (mNotification.tickerText != null) {
            			Log.d("Hive", mNotification.tickerText.toString());
            			if (Utils.isUserRequestForNotificationData()) {
            				Utils.publishEvent("Whatsapp: " + mNotification.tickerText.toString(), true);
            			} else {
            				Utils.publishEvent("Whatsapp message", true);
            			}
            		}
            	}
            }
    	}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    	
    }

}
