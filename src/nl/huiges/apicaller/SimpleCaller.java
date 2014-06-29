package nl.huiges.apicaller;

import android.os.Bundle;

/**
 * Simple caller uses: calls the signal method to let the receiver of your result handle the response
 * Because this is async, you can add a signalId to identify the call
 * 
 * @author Nanne Huiges
 *
 */
public class SimpleCaller extends APICaller {
	/**
	 * Object that will handle the response
	 */
	private iAPIResultReceiver receiver;
	private int signalId;

	
	public SimpleCaller(iAPIResultReceiver receiver, int signalId, int method, int scheme,  String server, String path) {
		super(method, scheme, server, path);
		this.receiver = receiver;
		this.signalId = signalId;
	}

	@Override
	protected void onPostExecute(String result) {
		this.setLastResponseString(result);
		Bundle extras = new Bundle();
		extras.putString(APICaller.RESULT, result);
		receiver.signal(signalId, extras);
	}

}
