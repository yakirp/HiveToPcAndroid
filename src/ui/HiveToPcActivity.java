package ui;

import services.HiveAccessibilityService;
import services.NLService;
import utils.Constants;
import utils.IabHelper;
import utils.IabResult;
import utils.Inventory;
import utils.Purchase;
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

	// Does the user have the premium upgrade?
	boolean mIsPremium = false;

	// Does the user have an active subscription to the infinite gas plan?
	boolean mSubscribedToHive = false;

	// SKUs for our products: the premium upgrade (non-consumable) and gas
	// (consumable)
	static final String SKU_PREMIUM = "monthly_license";
	static final String SKU_GAS = "monthly_license";

	// SKU for our subscription (infinite gas)
	static final String HIVE_INFINITE_GAS = "monthly_license";

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	// Graphics for the gas gauge
	static int[] TANK_RES_IDS = { R.drawable.ic_delete,
			R.drawable.ic_dialog_alert, R.drawable.ic_dialog_map,
			R.drawable.ic_delete, R.drawable.ic_dialog_info };

	// How many units (1/4 tank is our unit) fill in the tank.
	static final int TANK_MAX = 4;

	// Current amount of gas in tank, in units
	int mTank;

	// The helper object
	IabHelper mHelper;

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

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the infinite  plan?
			Purchase infinitePurchase = inventory
					.getPurchase(HIVE_INFINITE_GAS);
			mSubscribedToHive = (infinitePurchase != null && verifyDeveloperPayload(infinitePurchase));
			Log.d(TAG, "User " + (mSubscribedToHive ? "HAS" : "DOES NOT HAVE")
					+ " infinite subscription.");

			setWaitScreen(false);

			if (mSubscribedToHive) {
				// Display the fragment as the main content.
				getFragmentManager().beginTransaction()
						.replace(android.R.id.content, new SettingsFragment())
						.commit();
				//

				Utils.publishEvent("Hi... i'm here.", false);

			}

		}
	};

	// "Subscribe to infinite" button clicked. Explain to user, then start
	// purchase
	// flow for subscription.
	public void onSubscribeButtonClicked(View arg0) {
		if (!mHelper.subscriptionsSupported()) {
			complain("Subscriptions not supported on your device yet. Sorry!");
			return;
		}

		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";

		setWaitScreen(true);
		Log.d(TAG, "Launching purchase flow for infinite subscription.");
		mHelper.launchPurchaseFlow(this, HIVE_INFINITE_GAS,
				IabHelper.ITEM_TYPE_SUBS, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				setWaitScreen(false);
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(HIVE_INFINITE_GAS)) {
				// bought the infinite gas subscription
				Log.d(TAG, "Infinite subscription purchased.");
				alert("Thank you for subscribing to infinite !");
				mSubscribedToHive = true;
//				mTank = TANK_MAX;
//				updateUi();
//				setWaitScreen(false);
				
				
				getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
				
				
				
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit

			} else {
				complain("Error while consuming: " + result);
			}

			setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	public void onDestroy() {
		super.onDestroy();

		// very important:
		Log.d(TAG, "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	// updates UI to reflect model
	public void updateUi() {
	
	
  
	  
  
		
	}

	// Enables or disables the "please wait" screen.
	void setWaitScreen(boolean set) {
		findViewById(com.hivetopc.R.id.screen_main).setVisibility(
				set ? View.GONE : View.VISIBLE);
		findViewById(com.hivetopc.R.id.screen_wait).setVisibility(
				set ? View.VISIBLE : View.GONE);
	}

	void complain(String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	void saveData() {

		/*
		 * WARNING: on a real application, we recommend you save data in a
		 * secure way to prevent tampering. For simplicity in this sample, we
		 * simply store the data using a SharedPreferences.
		 */

	}

	void loadData() {

	}

}
