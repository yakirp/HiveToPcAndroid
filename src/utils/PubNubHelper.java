package utils;

import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

public class PubNubHelper {
	private Pubnub pubnub;
	private String pubKey = "demo";
	private String subKey = "demo";

	private static PubNubHelper instance;

	public static PubNubHelper getInstance() {
		if (instance == null) {
			instance = new PubNubHelper();
		}
		return instance;
	}

	 
	private PubNubHelper() {
		super();
		this.pubnub = new Pubnub(pubKey, subKey);
	}
	
	public void publish(String channel , String message) {
		
		pubnub.publish(channel, message, new Callback() {
		});
		
	}
	
	public void publish(String channel , JSONObject json) {
		
		pubnub.publish(channel, json, new Callback() {
		});
		
	}
	
	
	
	

}
