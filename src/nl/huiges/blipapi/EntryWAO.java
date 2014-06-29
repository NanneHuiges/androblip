package nl.huiges.blipapi;

import java.util.ArrayList;
import java.util.List;

import nl.huiges.apicaller.iAPIResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.URLSpan;

/**
 * 
 * Unsure if WAO is a thing, but it should be like a DAO, but then for the web.
 * And its not really an access object either. sue me.
 * @author nanne
 *
 */
		
public class EntryWAO {
	private String entry_id;//	The unique ID of the entry.	String
	private String display_name;//	The display name of the entry's owner.	String
	private String journal_title;//	The title of the entry owner's journal.	String
	private int member;//	1 if the entry's owner is currently a Full Member, otherwise 0.	Integer
	private String date;//	The entry's date in YYYY-MM-DD format	String
	private String title;//	The title of the entry.	String
	private String description;//	The entry's descriptive text, which can contain basic HTML.	String
	private String image;//	The URL of the entry's standard image.	String
	private String large_image;//	The URL of the entry's large image if it is available, otherwise the value is null.	String
	private String thumbnail;//	The URL of the large color thumbnail for the image.	String
	private String URL;//	The permanent website URL for the entry.	String
	private int rating_total;//	The cumulative rating for the entry, or null if ratings have been disabled.	Integer
	private int rating_count;//	The number of times the entry has been rated, or null if ratings have been disabled.	Integer
	private ArrayList<String> tags;//	An array of tag strings for the entry, or null if tags have been disabled.	Array
	private int views;//	The number of times the entry has been viewed, or null if the view counter has been disabled.

	//extended
	private String raw_description;//	The entry's raw descriptive text, which may contain BBCode.	String

	//actions
	private int comment;//	1 if the user can comment on the entry, otherwise 0.	Integer
	private int comment_links;//	1 if the user can add links to comments on the entry, otherwise 0.	Integer
	private int subscribe;//	1 if the user can subscribe to the journal, otherwise 0.	Integer
	private int unsubscribe;//	1 if the user can unsubscribe from the journal, otherwise 0.	Integer
	private int favourite;//	1 if the user can favourite the entry, otherwise 0.	Integer
	private int rate;//	1 if the user can rate the entry, otherwise 0.	Integer
	private int modify;//	1 if the user can modify the entry, otherwise 0.	Integer
	private int delete;//	1 if the user can delete the entry, otherwise 0.	Integer

	//comments
	private List<EntryCommentWAO> comments;
	private int commentsCount;
	
	
	//ids
	private String next;
	private String prev;
	
	public String getEntry_id() {
		return entry_id;
	}
	public void setEntry_id(String entry_id) {
		this.entry_id = entry_id;
	}
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
	public int getMember() {
		return member;
	}
	public void setMember(int member) {
		this.member = member;
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
	public Spanned getDescriptionSpanned(){
		String desc = getDescription();
		if(desc == null){
			return Html.fromHtml("");
		}
		Spanned spantext = Html.fromHtml(desc);
		//TODO: some people link to '#large'. I doubt that this is an url
		Object[] spans = spantext.getSpans(0, spantext.length(), Object.class);
		for (Object span : spans) {
			if (span instanceof URLSpan) {
				URLSpan urlSpan = (URLSpan) span;

				if (!urlSpan.getURL().startsWith("http")) {
					if (urlSpan.getURL().startsWith("/")) {
						urlSpan = new URLSpan("http://www.blipfoto.com" + urlSpan.getURL());
					} else if (urlSpan.getURL().startsWith("#") || urlSpan.getURL().startsWith("?")) {
						//relative to the complete current url.
						//e.g. #large => http://www.blipfoto.com/entry/3703511#large
						urlSpan = new URLSpan("http://www.blipfoto.com/entry/"+getEntry_id() + "/" + urlSpan.getURL());
					} else {
						//relative to current path, e.g.
						//123456 => http://www.blipfoto.com/entry/123456
						urlSpan = new URLSpan("http://www.blipfoto.com/entry/"+ urlSpan.getURL());
					}
				}

				int start = spantext.getSpanStart(span);
				int end = spantext.getSpanEnd(span);
				int flags = spantext.getSpanFlags(span);				

				((Spannable) spantext).removeSpan(span);
				((Spannable) spantext).setSpan(urlSpan, start, end, flags);
			}
		}
		return spantext;


	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLarge_image() {
		return large_image;
	}
	public void setLarge_image(String large_image) {
		this.large_image = large_image;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public int getRating_total() {
		return rating_total;
	}
	public void setRating_total(int rating_total) {
		this.rating_total = rating_total;
	}
	public int getRating_count() {
		return rating_count;
	}
	public void setRating_count(int rating_count) {
		this.rating_count = rating_count;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public String getRaw_description() {
		return raw_description;
	}
	public void setRaw_description(String raw_description) {
		this.raw_description = raw_description;
	}

	/************start actions***************/
	public boolean canComment(){
		return this.comment == 1;
	}

	public boolean canCommmentLink(){
		return this.comment_links == 1;
	}

	public boolean canSubscribe(){
		return this.subscribe == 1 ;
	}

	public boolean canUnSubscribe(){
		return this.unsubscribe == 1;
	}

	public boolean canFavourite(){
		return this.favourite == 1;
	}
	public boolean canRate(){
		return this.rate == 1;
	}

	public boolean canModify(){
		return this.modify == 1;
	}
	public boolean canDelete(){
		return this.delete == 1;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public void setComment_links(int comment_links) {
		this.comment_links = comment_links;
	}
	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}
	public void setUnsubscribe(int unsubscribe) {
		this.unsubscribe = unsubscribe;
	}
	public void setFavourite(int favourite) {
		this.favourite = favourite;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public void setModify(int modify) {
		this.modify = modify;
	}
	public void setDelete(int delete) {
		this.delete = delete;
	}

	
	/************end actions***************/

	public void entryFromJSONString(String result) throws JSONException {
		JSONObject entryO = new JSONObject(result);
		//TODO check if error.

		JSONObject data = (JSONObject) entryO.getJSONObject("data");
		setEntry_id(data.getString("entry_id"));
		setDisplay_name(data.getString("display_name"));
		setJournal_title(data.getString("journal_title"));
		setMember(data.getInt("member"));
		setDate(data.getString("date"));
		setTitle(data.getString("title"));
		setDescription(data.getString("description"));
		setImage(data.getString("image"));
		setLarge_image(data.getString("large_image"));
		setThumbnail(data.getString("thumbnail"));
		setURL(data.getString("url"));
		setRating_total(data.getInt("rating_total"));
		setRating_count(data.getInt("rating_count"));
		//setTags(data.getString("tags"));
		setViews(data.getInt("views"));

		//TODO only if extended is actually requested?
		if(data.has("extended") && data.getJSONObject("extended").has("raw_descripiton")){
			setRaw_description(data.getJSONObject("extended").getString("raw_description"));
		}
		
		if(data.has("actions")){
			JSONObject actions = data.getJSONObject("actions");
			setComment(actions.getInt("comment"));
			setComment_links(actions.getInt("comment_links"));
			setSubscribe(actions.getInt("subscribe"));
			setUnsubscribe(actions.getInt("unsubscribe"));
			setFavourite(actions.getInt("favourite"));
			setRate(actions.getInt("rate"));
			setModify(actions.getInt("modify"));
			setDelete(actions.getInt("delete"));
		}

		if(data.has("ids")){
			JSONObject ids = data.getJSONObject("ids");
			setNext(ids.getString("next"));
			setPrev(ids.getString("previous"));
		}
		
		setComments(EntryCommentWAO.entryListFromJSONString(data));

	}
	
	private void setComments(List<EntryCommentWAO> comments) {
		this.comments=comments;
		this.commentsCount = EntryCommentWAO.getCommentsCountFromList(comments);
	}
	
	public List<EntryCommentWAO> getComments(){
		return this.comments;
	}
	
	public int getCommentsCount() {
		return this.commentsCount;
	}
	public String getPrev() {
		return prev;
	}
	public void setPrev(String prev) {
		if( ! "null".equals(prev)){
			this.prev = prev;
		}
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		if( ! "null".equals(next)){
			this.next = next;
		}
	}
	public boolean favourite(Context context, iAPIResultReceiver receiver, int signal) {
		if(this.canFavourite()){
			BlipPostFavourite bpf = new BlipPostFavourite(context);
			bpf.favourite(receiver,signal,this.entry_id);
			return true;
		}else{
			return false;
		}
	}
	public boolean subtoggle(Context context, iAPIResultReceiver receiver, int signal) {
		if(this.canSubscribe()){
			BlipPostSubscribe bpf = new BlipPostSubscribe(context);
			bpf.subscribe(receiver,signal,this.display_name);
			return true;
		}else if(this.canUnSubscribe()){
			BlipPostSubscribe bpf = new BlipPostSubscribe(context);
			bpf.unsubscribe(receiver,signal,this.display_name);
			return true;
		}else{
			return false;
		}		
	}



}
