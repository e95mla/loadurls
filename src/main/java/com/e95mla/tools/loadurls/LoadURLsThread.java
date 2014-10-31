package com.e95mla.tools.loadurls;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class LoadURLsThread extends Thread {
	
	private int threadId;
	private int requestDelay;
	private long totalTime;
	private String envPath;
	private List<String> urls;
	private boolean writeResultToFile;
	
	private static final Logger logger = Logger.getLogger(LoadURLsThread.class);
	private static final Logger tracer = Logger.getLogger("tracer");

	public LoadURLsThread(int threadId, int requestDelay, long totalTime, String envPath, List<String> urls, boolean writeResultToFile) throws MalformedURLException, IOException, InterruptedException {
		this.threadId = threadId;
		this.requestDelay = requestDelay;
		this.totalTime = totalTime;
		this.envPath = envPath;
		this.urls = urls;
		this.writeResultToFile = writeResultToFile;
	}
	
	public void run(){
		FileOutputStream fos = null;
		long endTime = System.currentTimeMillis() + totalTime;
		
		logger.debug("THREAD " + threadId + " started.");
		if(this.writeResultToFile){
			try {
				fos = new FileOutputStream("Thread_" + this.threadId + "_SearchResults_" + System.currentTimeMillis() + ".xml");
			} catch (FileNotFoundException e) {
				logger.debug(e, e);
			}
		}
		
		Iterator<String> urlIter = urls.iterator();
		while(System.currentTimeMillis() < endTime){
			if(!urlIter.hasNext()){
				urlIter = urls.iterator();
			}
			String url = envPath + urlIter.next();
			long t0 = System.currentTimeMillis();
			
			long bytes = -1;
			
			try {
				bytes = getFileFromURL(new URL(url), fos);
			} catch (Exception e) {
				logger.debug(e, e);
			}
			
			long t1 = System.currentTimeMillis();
			
			//tracer.info("time=" + (t1-t0) + " , bytes=" + bytes +" , url=" + url);
			tracer.info((t1-t0) + "|" + bytes +"|" + url);
			
			try {
				Thread.sleep(requestDelay);
			} catch (InterruptedException e) {
				logger.debug(e, e);
			}
		}
		logger.debug("THREAD " + threadId + " STOPPED.");
		if(this.writeResultToFile){
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				logger.debug(e, e);
			}
		}
	}
	
	private long getFileFromURL(URL url, FileOutputStream fos) throws IOException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = url.openStream();
			byte[] byteChunk = new byte[4096];

			int n;
			while ((n = is.read(byteChunk)) > 0) {
				bais.write(byteChunk, 0, n);
			}
		} catch (IOException e) {
			logger.debug("Failed while reading bytes");
			logger.debug(e, e);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		int size = bais.toByteArray().length;
		if(this.writeResultToFile){
			//bais.writeTo(System.out);
			bais.writeTo(fos);
		}
		bais.flush();
		bais.close();

		return size;
	}

}
