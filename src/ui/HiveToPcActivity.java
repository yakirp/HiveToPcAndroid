package ui;

import services.HiveAccessibilityService;
import services.NLService;
import utils.Constants;
import utils.Utils;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class HiveToPcActivity extends Activity {

	protected static final String TAG = "hive";

	 

	// SKUs for our products: the premium upgrade (non-consumable) and gas
	// (consumable)
	static final String SKU_PREMIUM = "monthly_license";
	static final String SKU_GAS = "monthly_license";

	 

	 

	 
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
		
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			Log.d(TAG, "Current build version is bigger than 19");
    		if (!Utils.isNotificationSettingsEnabled()) {
    			Log.d(TAG, "NotificationSettings is disabled"); 
    			startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    		}
    	} else if (android.os.Build.VERSION.SDK_INT < 19) {
    		Log.d(TAG, "Current build version is less than 19");
    		if (HiveAccessibilityService.isAccessibilitySettingsOn()) {
    			Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
    			startActivityForResult(intent, 0);
    		}
    	}
		
//		setContentView(com.hivetopc.R.layout.mainlayout);
//
//		// compute your public key and store it in base64EncodedPublicKey
//		mHelper = new IabHelper(this, Constants.base64EncodedPublicKey);
//
//		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//
//			@Override
//			public void onIabSetupFinished(IabResult result) {
//				if (!result.isSuccess()) {
//					// Oh noes, there was a problem.
//					Log.d("dd", "Problem setting up In-app Billing: " + result);
//				}
//				// Have we been disposed of in the meantime? If so, quit.
//				if (mHelper == null)
//					return;
//
//				// IAB is fully set up. Now, let's get an inventory of stuff we
//				// own.
//				Log.d(TAG, "Setup successful. Querying inventory.");
//				mHelper.queryInventoryAsync(mGotInventoryListener);
//
//			}
//		});

	}

 
 
  

	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	public void onDestroy() {
		super.onDestroy();

		 
	}

 
	 

}
