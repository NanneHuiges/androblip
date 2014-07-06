package nl.huiges.blipapi;

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



}
