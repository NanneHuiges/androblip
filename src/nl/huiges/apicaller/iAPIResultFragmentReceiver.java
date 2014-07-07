package nl.huiges.apicaller;

import android.support.v4.app.DialogFragment;

public interface iAPIResultFragmentReceiver {
	public void addFragment(DialogFragment blipViewFragment, String tag);
	public void failed();
}
