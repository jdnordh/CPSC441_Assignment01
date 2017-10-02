package client;

import java.util.Date;

public class WebObject {
	String url;
	String file;
	
	/** Recorded system time compared to current time */
	long lastModified;
	
	/**
	 * Constructor
	 * @param um Url
	 * @param f File String
	 */
	public WebObject(String um, String f){
		url = um;
		Date date = new Date();
		lastModified = date.getTime();
		file = f;
	}
	
	/**
	 * Set the Url
	 * @param newurl Url formatted as "hostname[:port]/pathname"
	 */
	public void setUrl(String newurl){
		url = newurl;
	}
	
	/**
	 * @return The Url
	 */
	public String getUrl(){
		return url;
	}
	
	/**
	 * Get the time at which the object was last modified
	 * @return
	 */
	public long lastModified(){
		return lastModified;
	}
	
	public String getFile(){
		if (file != null) return file;
		return "No file is recorded";
	}
	
	/**
	 * Update the last usage time
	 */
	public void use(){
		lastModified = System.nanoTime() / 1000;
	}
}
