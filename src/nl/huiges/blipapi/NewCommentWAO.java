package nl.huiges.blipapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;


public class NewCommentWAO {
	private int 		comment_id; //	The unique ID of the comment.	Integer
	private String 		display_name; //	The display name of the commentor.	String
	private String 		content; //	The comment, which may contain basic HTML.	String
	private Set<String> icons; //	An array of icon names applicable to the commentor. To obtain the icon URL, prepend the icon name with http://www.blipfoto.com/_images/user_icons/.	Array
	private int 		reply; //	When return_comments is set to 2, this property indicates if the authenticated user can reply to the comment.	Integer
	private String		Thumbnail;
	private String		entry_id;
	
	public static List<NewCommentWAO> entryListFromJSONString(JSONArray data) throws JSONException {
		ArrayList<NewCommentWAO> comments = new ArrayList<NewCommentWAO>();

		
			for(int i = 0; i < data.length(); i++){
				comments.add(NewCommentWAO.entryFromJSONObject(data.getJSONObject(i)));
			}
		
		return comments;
	}

	private static NewCommentWAO entryFromJSONObject(JSONObject commentJO) throws JSONException {
		NewCommentWAO comment = new NewCommentWAO();

		comment.setComment_id(commentJO.getInt("comment_id"));
		comment.setDisplay_name(commentJO.getString("display_name"));
		comment.setContent(commentJO.getString("content"));

		if(commentJO.has("icons")){
			comment.setIcons(commentJO.getJSONArray("icons"));
		}
		if(commentJO.has("reply")){
			comment.setReply(commentJO.getInt("reply"));
		}
		if(commentJO.has("thumbnail")){
			comment.setThumbnail(commentJO.getString("thumbnail"));
		}else{
			comment.setThumbnail("");
		}
		if(commentJO.has("entry_id")){
			comment.setEntry_id(commentJO.getString("entry_id"));
		}
		return comment;
	}


	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	private String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Set<String> getIcons() {
		return icons;
	}

	public void setIcons(JSONArray iconsJA) throws JSONException {
		this.icons = new HashSet<String>();
		if(iconsJA != null){
			for(int i = 0; i < iconsJA.length(); i++){
				this.icons.add(iconsJA.getString(i));
			}
		}
	}

	public int getReply() {
		return reply;
	}

	public void setReply(Integer reply) {
		if(reply == null ){
			this.reply = 0;
		}
		this.reply = reply;
	}


	public Spanned getContentSpanned() {
		Spanned spantext = Html.fromHtml(getContent());
		Object[] spans = spantext.getSpans(0, spantext.length(), Object.class);
		for (Object span : spans) {
			if (span instanceof URLSpan) {
				URLSpan urlSpan = (URLSpan) span;

				int start = spantext.getSpanStart(span);
				int end = spantext.getSpanEnd(span);
				int flags = spantext.getSpanFlags(span);				

				((Spannable) spantext).removeSpan(span);
				((Spannable) spantext).setSpan(urlSpan, start, end, flags);
			}
		}
		return spantext;


	}

	public Spanned getDisplay_nameSpanned() {
		SpannableString spanString = new SpannableString("~"+this.display_name);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
		return spanString;
		}

	public String getThumbnail() {
		return Thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		Thumbnail = thumbnail;
	}

	public String getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(String entry_id) {
		this.entry_id = entry_id;
	}
}


