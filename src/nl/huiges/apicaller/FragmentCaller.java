package nl.huiges.apicaller;

/**
 * Apicaller that sends result to a fragment.
 * 
 * @author nanne
 *
 */
public class FragmentCaller extends APICaller {
	private iAPIResultFragment fragment;
	private iAPIResultFragmentReceiver activity;

	public FragmentCaller(iAPIResultFragmentReceiver activity, iAPIResultFragment fragment, 
							int method, int scheme,  String server, String path) {
		super(method, scheme, server, path);
		this.fragment = fragment;
		this.activity = activity;
	}

	@Override
	protected void onPostExecute(String result) {
		this.setLastResponseString(result);		
		fragment.showFromResult(activity, result);		
	}

}
