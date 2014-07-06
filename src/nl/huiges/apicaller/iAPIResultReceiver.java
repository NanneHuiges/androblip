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
	public void signal(int signalId, Bundle extras);
}
