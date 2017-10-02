package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.ArrayList;

public class UrlCache {

	ArrayList<WebObject> catalog;
	final File folder = new File("");
	File [] fileCatalog;
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw runtime exception.
	 *
     * @throws IOException if encounters any errors/exceptions
     */
	public UrlCache() throws IOException {
		//TODO initilize a file array catalog
		catalog = new ArrayList<WebObject>();
		fileCatalog = folder.listFiles();
		System.out.println("Files already in the system:");
		for (int i = 0; i < fileCatalog.length; i++){
			System.out.println(fileCatalog[i].getName());
		}
	}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws IOException if encounters any errors/exceptions
     */
	public void getObject(String url) throws IOException {
		
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
		// check if the url is already cached
		boolean haveit = false;
		for (int i = 0; i < catalog.size(); i++){
			if (catalog.get(i).getUrl().equals(url)){
				// check if the file is too old (1 day in this case)
				Date date = new Date();
				long timeNow = date.getTime();
				if ( (timeNow - catalog.get(i).lastModified()) > 86400) {
					// file is too old, get a new version
					break;
				}
				haveit = true;
				System.out.println("Don't need to get new version of: " + url);
				break;
			}
		}
		if (!haveit){
			//TODO get it
			System.out.println("Need to get " + url);
			try {
				InputStream in;
				PrintWriter out;
				System.out.println("Trying to connect to: " + server);
				System.out.println("Trying to download: " + object);
				
				Socket socket = new Socket(InetAddress.getByName(server), port);
				in = socket.getInputStream();
				out = new PrintWriter(socket.getOutputStream());
				
				// send the request
				out.println("GET " + object + " HTTP/1.1\r");
				out.println("Host: " + server + "\r");
				out.println("\r");
				out.flush();
				
				/*
				System.out.println("The request sent was:");
				System.out.println("GET " + object + " HTTP/1.1");
				System.out.println("Host: " + server);
				*/
				
				/*
				StringBuffer file = new StringBuffer();
				String buffer;
				*/
				
				
				
				String filename = server.replace('.', '_') + object.replace('/', '_');
			
				
				
				byte [] byteArray = new byte[1024 * 64];
				int index = 0;
				int headerEnd = 0;
				while ( (byteArray[index++] = (byte)in.read()) != -1){
					if (byteArray[index - 1] == '\r'){
						headerEnd = 1;
					}
					if (headerEnd == 1 && byteArray[index - 1] == '\n'){
						headerEnd = 2;
						// Header is done, time to start reading into a file
						// TODO ^^^
					}
				}
				
//				int index = 0;
//				while ((byteArray[index++] = (byte) in.read(byteArray, 0, byteArray.length)) != -1){
//					if (index > byteArray.length -1) throw new Exception("File is too big");
//				}
//				
//				
//				System.out.println("The file downloaded was: \n" + byteArray.toString() + "\n\n");
//				// create object and add it to the catalog
//				WebObject download = new WebObject(url, byteArray.toString());
//				catalog.add(download);
//				
//				// TODO parse the header
//				// TODO write file from byte array
//				String filename = server.replace('.', '_') + object.replace('/', '_');
//				
//				FileOutputStream fos = new FileOutputStream("files\\" + filename);
//				try {
//					fos.write(byteArray);
//				} finally {
//					fos.close();
//				}
				
				
				socket.close();
			} catch (Exception e) { 
				//System.err.println("Failed to connect to: " + url);
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
