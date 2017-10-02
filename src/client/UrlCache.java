package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class UrlCache {

	ArrayList<WebObject> catalog;
	
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw runtime exception.
	 *
     * @throws IOException if encounters any errors/exceptions
     */
	public UrlCache() throws IOException {
		catalog = new ArrayList<WebObject>();
	}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws IOException if encounters any errors/exceptions
     */
	public void getObject(String url) throws IOException {
		
		/*
		 * String[] url = {"people.ucalgary.ca/~mghaderi/index.html",
						"people.ucalgary.ca/~mghaderi/test/uc.gif",
						"people.ucalgary.ca/~mghaderi/test/a.pdf",
						"people.ucalgary.ca:80/~mghaderi/test/test.html"};
		 * 
		 */
		//TODO url stuff
		
		String server = "";
		String object = "";
		int port = 80;
		
		// Parse the url
		int colon = -1;
		int firstslash = -1;
		boolean serverdone = false;
		for (int i = 0; i < url.length(); i++){
			if (url.charAt(i) == '/' || url.charAt(i) == ':') serverdone = true;
			if (!serverdone){
				server += url.charAt(i);
			}
			if (url.charAt(i) == ':' && colon < 0) colon = i;
			if (url.charAt(i) == '/' && firstslash < 0) firstslash = i;
		}
		if (firstslash > 0){
			object = url.substring(firstslash);
		}
		if (colon > 0 && firstslash > 0){
			String portNumber = url.substring(colon + 1, firstslash);
			port = Integer.parseInt(portNumber);
		}
		boolean haveit = false;
		for (int i = 0; i < catalog.size(); i++){
			if (catalog.get(i).getUrl().equals(url)){
				haveit = true;
				System.out.println("Don't need to get " + url);
				break;
			}
		}
		if (!haveit){
			//TODO get it
			System.out.println("Need to get " + url);
			try {
				BufferedReader in;
				PrintWriter out;
				System.out.println("Trying to connect to: " + server);
				System.out.println("Trying to download: " + object);
				
				Socket socket = new Socket(InetAddress.getByName(server), port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
				
				// send the request
				out.println("GET " + object + " HTTP/1.1\r");
				out.println("Host: " + server + "\r");
				out.println("\r");
				out.flush();
				
				System.out.println("The request sent was:");
				System.out.println("GET " + object + " HTTP/1.1");
				System.out.println("Host: " + server);
				
				StringBuffer file = new StringBuffer();
				String buffer;
				
				while ((buffer = in.readLine()) != null){
					file.append(buffer);
					file.append('\n');
				}
				System.out.println("The file downloaded was: \n" + file.toString() + "\n\n");
				// create object and add it to the catalog
				WebObject download = new WebObject(url, file.toString());
				catalog.add(download);
				
				socket.close();
			} catch (Exception e) { 
				System.err.println("Failed to connect to: " + url);
				System.err.println("Error: "+ e.getMessage());
			}
		}
	}
	
    /**
     * Returns the Last-Modified time associated with the object specified by the parameter url.
	 * Returns -1 if Url is not found
     * @param url 	URL of the object 
	 * @return the Last-Modified time in millisecond as in Date.getTime()
     */
	public long getLastModified(String url) {
		long millis = -1;
		for (int i = 0; i < catalog.size(); i++){
			if (catalog.get(i).getUrl().equals(url)){
				return catalog.get(i).lastModified();
			}
		}
		return millis;
	}
	
	/**
	 * Get the file stored in the web object
	 * @param url Url 
	 * @return File in string
	 */
	public String getFile(String url){
		for (int i = 0; i < catalog.size(); i++){
			if (catalog.get(i).getUrl().equals(url)){
				return catalog.get(i).getFile();
			}
		}
		return "Url not found";
	}
	
}
