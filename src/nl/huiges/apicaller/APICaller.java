package nl.huiges.apicaller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import nl.huiges.blipapi.Settings;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.huiges.AndroBlip.C;


/**
 * Class to help with simple API calls.
 * Tried to keep this as separate from the current usecase 'androblip'
 * but there has been some leaking.
 * 
 * @author Nanne Huiges
 *
 */
public abstract class APICaller extends AsyncTask<Void, Integer, String>{
	public static final String RESULT = "resultstring";
	public static final int METHOD_POST = 1;
	public static final int METHOD_GET = 2;
	public static final int METHOD_PUT = 3;
	public static final int METHOD_DELETE = 4;
	public static final int SCHEME_HTTP = 1;
	public static final int SCHEME_HTTPS = 2;
	
	private int method;
	private int scheme;
	private String server;
	private String path;

	private ArrayList<NameValuePair> parameters;
	private ArrayList<NameValuePair> files; //TODO: define what these are (uris?)

	private String lastResponseString;
	private String errorMessage;
 	
	/**
	 * 
	 * @param Method one of APICaller.METHOD_POST, METHOD_PUT, METHOD_GET or METHOD_DELETE
	 * @param Scheme one of APICaller.SCHEME_HTTP or SCHEME_HTTPS
	 * @param server the authority url, e.g. www.huiges.nl
	 * @param path the path to call
	 */
	public APICaller(int method, int scheme, String server, String path) {
		this.method = method;
		this.scheme = scheme;
		this.server = server;
		this.path   = path;
		
		this.parameters = new ArrayList<NameValuePair>();
		this.files  = new ArrayList<NameValuePair>();
		this.addParameter("api_key", Settings.API_KEY);
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	protected abstract void onPostExecute(String result);

	public void addFilePath(String path) {
		this.files.add(new BasicNameValuePair("image",path));
	}
	
	public void addParameter(String name, String value) {
		this.parameters.add(new BasicNameValuePair(name, value));
	}

	public void addParameter(String name, Integer value) {
		this.parameters.add(new BasicNameValuePair(name, value.toString()));
	}

	public void addParameter(String name, Long value) {
		this.parameters.add(new BasicNameValuePair(name, value.toString()));
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public String getLastResponseString() {
		return lastResponseString;
	}

	protected void setLastResponseString(String lastResponseString) {
		this.lastResponseString = lastResponseString;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	protected String doInBackground(Void... params) {
		boolean result;
		try {
			switch (this.method){
			case METHOD_GET:
				result = this.doGet();
				break;
			case METHOD_POST:
				result = this.doPost();
				break;
			case METHOD_DELETE:
				result = this.doDelete();
				break;
			default:
				throw new UnsupportedOperationException("Not Implemented");
			}

			if ( ! result ) {
				this.setErrorMessage("Call failed.");
			}
			
		} catch (MalformedURLException e) {
			this.setErrorMessage("MalformedURLException: " + e.getMessage());;
		} catch (URISyntaxException e) {
			this.setErrorMessage("URISyntaxException: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			this.setErrorMessage("UnsupportedEncodingException: " + e.getMessage());
		}
		
		return this.getLastResponseString();
	}
	
	private Uri.Builder getBuilder(){
		String scheme = ( this.scheme == SCHEME_HTTP ? "http" : "https" );
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(scheme)
				.authority(server)
				.encodedPath(path);	
		return builder;
	}
	
	protected boolean doDelete() throws MalformedURLException, URISyntaxException{
		Uri.Builder builder = getBuilder();
		Uri semiUri = builder.build();
		String paramString = URLEncodedUtils.format(this.parameters, "utf-8");
		String fullURI =  semiUri +"?"+ paramString;
		
		if(C.VERBOSE){Log.d(C.TAG,"APICaller get: "+fullURI);}
		
		HttpDelete req = new HttpDelete(fullURI);
		return this.call(req);
	}
	
	protected boolean doGet() throws MalformedURLException, URISyntaxException{
		Uri.Builder builder = getBuilder();
		Uri semiUri = builder.build();
		String paramString = URLEncodedUtils.format(this.parameters, "utf-8");
		String fullURI =  semiUri +"?"+ paramString;
		
		if(C.VERBOSE){Log.d(C.TAG,"APICaller get: "+fullURI);}
		
		HttpGet req = new HttpGet(fullURI);
		return this.call(req);
	}
	
	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	protected boolean doPost() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException{
		if(C.VERBOSE){Log.d(C.TAG,"APICaller POST ");}
		Uri.Builder mbuilder = getBuilder();
		HttpPost post = new HttpPost(mbuilder.toString());

		//http://stackoverflow.com/questions/18964288/upload-a-file-through-an-http-form-via-multipartentitybuilder-with-a-progress
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		Iterator<NameValuePair> it ;
		
		it = parameters.iterator();
		while(it.hasNext()){
			NameValuePair nvp = it.next();
			if( nvp.getValue() != null ){
				if(C.VERBOSE){Log.d(C.TAG,"post name/value "+nvp.getName()+"/"+nvp.getValue());}
				builder.addTextBody(nvp.getName(), nvp.getValue());
			}
		}
		
		File file;
		it = files.iterator();
		while(it.hasNext()){
			NameValuePair nvp = it.next();
		    file = new File(nvp.getValue());
		    FileBody fb = new FileBody(file);
		    builder.addPart(nvp.getName(), fb);  
		    if(C.VERBOSE){Log.d(C.TAG,"post file: "+nvp.getName());}
		}
		
		final HttpEntity yourEntity = builder.build();

		    class ProgressiveEntity implements HttpEntity {
		        @Override
		        public void consumeContent() throws IOException {
		            yourEntity.consumeContent();                
		        }
		        @Override
		        public InputStream getContent() throws IOException,
		                IllegalStateException {
		            return yourEntity.getContent();
		        }
		        @Override
		        public Header getContentEncoding() {             
		            return yourEntity.getContentEncoding();
		        }
		        @Override
		        public long getContentLength() {
		            return yourEntity.getContentLength();
		        }
		        @Override
		        public Header getContentType() {
		            return yourEntity.getContentType();
		        }
		        @Override
		        public boolean isChunked() {             
		            return yourEntity.isChunked();
		        }
		        @Override
		        public boolean isRepeatable() {
		            return yourEntity.isRepeatable();
		        }
		        @Override
		        public boolean isStreaming() {             
		            return yourEntity.isStreaming();
		        } // CONSIDER put a _real_ delegator into here!

		        @Override
		        public void writeTo(OutputStream outstream) throws IOException {

		            class ProxyOutputStream extends FilterOutputStream {
		                /**
		                 * @author Stephen Colebourne
		                 */

		                public ProxyOutputStream(OutputStream proxy) {
		                    super(proxy);    
		                }
		                public void write(int idx) throws IOException {
		                    out.write(idx);
		                }
		                public void write(byte[] bts) throws IOException {
		                    out.write(bts);
		                }
		                public void write(byte[] bts, int st, int end) throws IOException {
		                    out.write(bts, st, end);
		                }
		                public void flush() throws IOException {
		                    out.flush();
		                }
		                public void close() throws IOException {
		                    out.close();
		                }
		            } // CONSIDER import this class (and risk more Jar File Hell)

		            class ProgressiveOutputStream extends ProxyOutputStream {
		                public ProgressiveOutputStream(OutputStream proxy) {
		                    super(proxy);
		                }
		                public void write(byte[] bts, int st, int end) throws IOException {
		                    out.write(bts, st, end);
		                }
		            }

		            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
		        }
		    };
		    
		    ProgressiveEntity myEntity = new ProgressiveEntity();

		    post.setEntity(myEntity);
		    //HttpResponse response = client.execute(post);        

		    return call(post);	
	}
	
	
	
	private boolean call(HttpUriRequest req) throws MalformedURLException, URISyntaxException {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpResponse response = httpclient.execute(req);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				this.setLastResponseString(out.toString());
				return true;
			} else {
				response.getEntity().getContent().close();
				this.setErrorMessage(statusLine.getReasonPhrase());
				return false;
			}
		} catch (IOException e) {
			this.setErrorMessage("IOExcpetion: " + e.getMessage());
			return false;
		}
	}	

}
