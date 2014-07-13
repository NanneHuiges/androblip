package nl.huiges.apicaller;

import android.os.Bundle;

/**
 * Defines a method that the result gets send to.
 * Simplest way to perform (async) api calls.
 * 
 * @author Nanne Huiges
 *
 */
public interface iAPIResultReceiver {
	/**
	 * Handles result of an API-call
	 * 
	 * @param signalId type of result or way to handle the result
	 * @param extras contents of the result, depends on the call
	 */
	public void signal(int signalId, Bundle extras);
	
	/**
	 * Handles an error situation.
	 * (no network, unexpected result, etc)
	 * @param message TODO
	 */
	public void showError(CharSequence message);
}
