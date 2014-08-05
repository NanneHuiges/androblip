package nl.huiges.blipapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;

import com.huiges.AndroBlip.R;

import nl.huiges.apicaller.APICaller;

/**
 * Wrapper class to communicate with the blipfoto api. 
 * Combines all API calls as static functions.
 * It might need some splitting in the end.
 * 
 * Results get their own DAO-like objects (WAO: Web Access Objects)
 * These objects are probably going to go grow in time, so sorry about that as well..
 * 
 * Uses separate 'apicaller' for HTTP connection.
 * 
 * This all should probably be a separate library?
 * 
 * @author Nanne Huiges
 * @see APICaller
 * @version 0.1.1
 */
public class BlipAPI {

	public static final String VIEW_ALL   = "everything";
	public static final String VIEW_SPOT  = "spotlight";
	public static final String VIEW_RATED = "rated";
	public static final String VIEW_RAND  = "random";
	public static final String VIEW_SUBS  = "subscribed";
	public static final String VIEW_ME    = "me";
	public static final String VIEW_FAV   = "favourites";
	public static final String VIEW_MYFAV = "myfavourites";
	
	public static final int SIZE_SMALL  = 0;
	public static final int SIZE_BIG    = 1;
	public static final int THUMB_COLOR = 0;
	public static final int THUMB_GREY  = 1;


	public static final String server     = "api.blipfoto.com";

	/**
	 * API code (iApiResultReceiver) makes the result 
	 * go straight to the receiver's signal function
	 * 
	 * This is unfortunate as we want to parse it first
	 * and have the parsing be done by this object. 
	 * 
	 * Quick fix was below static function
	 * @todo call receivers signal function with parsed bundle
	 * 
	 * @param extras result bundle as received from API call
	 * @return parsed bundle
	 */
	public static Bundle parseResult(Bundle extras, Resources res){
		String resultS = extras.getString(APICaller.RESULT);
		JSONObject entryO=null;

		Bundle result = new Bundle();
		
		try {
			entryO = new JSONObject(resultS);
		// } catch (JSONException | NullPointerException e1) { java 7 says hi
		} catch (JSONException e1) {
			result.putBoolean("error", true);
			result.putString("resultText",res.getString(R.string.error_upload_unknown));
			return result;
		} catch ( NullPointerException e1) {
			result.putBoolean("error", true);
			result.putString("resultText",res.getString(R.string.error_upload_network));
			return result;
		}
		
		String resultText = "";
		boolean error = false;
		if(entryO != null){
			JSONObject jso = null;
			try {
				jso = (JSONObject) entryO.getJSONObject("error");
			} catch (JSONException e1) {}

			if (jso != null){
				try {
					resultText = jso.getString("message");
				} catch (JSONException e) {
					resultText = res.getString(R.string.error_blipfoto_empty);
				}
				result.putBoolean("error", true);
				result.putString("resultText",resultText);
				return result;
				
			}else{
				//geen error. hopelijk wel data dan.
				try {
					JSONObject data = (JSONObject) entryO.getJSONObject("data");
					resultText  = data.getString("message");
				} catch (JSONException e) {
					error = true;
					resultText = res.getString(R.string.error_blipfoto_unsure);
				}
			}
		}
		
		result.putBoolean("error", error);
		result.putString("resultText",resultText);
		return result;
	}

}
