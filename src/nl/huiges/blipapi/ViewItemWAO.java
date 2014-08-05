package nl.huiges.blipapi;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewItemWAO {
	private String display_name;//	The display name of the entry owner.	String
	private String journal_title;//	The title of the related journal.	String
	private String entry_id;//	The unique ID of the entry.	String
	private String thumbnail;//	The URL of the entry's thumbnail image.	String
	private String date;//	The entry date in YYYY-MM-DD format.	String
	private String title;//	The entry title.	String
	private String url;//	The entry's website URL.	String
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getJournal_title() {
		return journal_title;
	}
	public void setJournal_title(String journal_title) {
		this.journal_title = journal_title;
	}
	public String getEntry_id() {
		return entry_id;
	}
	public void setEntry_id(String entry_id) {
		this.entry_id = entry_id;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public static ArrayList<ViewItemWAO> getEmptyList(){
		ArrayList<ViewItemWAO> bvil = new ArrayList<ViewItemWAO>();
		ViewItemWAO bvi = new ViewItemWAO();
		bvi.setDate("1970-01-01");
		bvi.setDisplay_name("-");
		bvi.setEntry_id("-1");
		bvi.setJournal_title("-");
		bvi.setTitle("Sorry, no title");
		bvil.add(bvi);
		return bvil;
	}
	
	public static ArrayList<ViewItemWAO> listFromJSONString(String result) throws JSONException {
		ArrayList<ViewItemWAO> bvil = new ArrayList<ViewItemWAO>();
		JSONObject resultO = new JSONObject(result);
		JSONArray resultA = (JSONArray) resultO.get("data");
		ViewItemWAO bvi = null;
		for (int i = 0; i < resultA.length(); ++i) {
		    JSONObject res = resultA.getJSONObject(i);
		    bvi = ViewItemWAO.fromJSONObject(res);
		    bvil.add(bvi);
		}
		return bvil;

	}
	
	/**
	 * @todo check if exception could be thrown?
	 * @param res
	 * @return
	 */
	private static ViewItemWAO fromJSONObject(JSONObject res) {
		ViewItemWAO bvi = new ViewItemWAO();
		try {
			bvi.setDate(res.getString("date"));
			bvi.setDisplay_name(res.getString("display_name"));
			bvi.setEntry_id(res.getString("entry_id"));
			bvi.setJournal_title(res.getString("journal_title"));
			bvi.setThumbnail(res.getString("thumbnail"));
			bvi.setTitle(res.getString("title"));
			bvi.setUrl(res.getString("url"));
		} catch (JSONException e) {
		}		
		return bvi;
	}
}
