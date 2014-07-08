package nl.huiges.apicaller;

import android.support.v4.app.DialogFragment;

public interface iAPIResultFragmentReceiver {
	
	public void addFragment(DialogFragment blipViewFragment, String tag);

	/**
	 * Handles an error situation.
	 * (no network, unexpected result, etc)
	 */
	public void showError();
}
