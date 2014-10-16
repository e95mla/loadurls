package com.e95mla.tools.loadurls;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class LoadURLs {
	
	private static final Logger logger = Logger.getLogger(LoadURLs.class);
	
	private static String ENV_PATH = "";
	private static int NUMBER_OF_THREADS = 3;
	private static long TOTAL_EXECUTION_TIME = 600000;
	private static int REQUEST_DELAY = 500;
	private static boolean WRITE_RESULT_TO_FILE = false;
	private static String URL_INPUT_FILE = "url_input_file.txt";
	
	private static String PROPERTY_FILE = "loadurls.properties";
	
	private List<String> urls = new ArrayList<String>();

	public LoadURLs() throws IOException, InterruptedException{
		
		initProperties();
		
		BufferedReader reader = new BufferedReader(new FileReader(URL_INPUT_FILE));
		String line = null;
		while ((line = reader.readLine()) != null) {
		    urls.add(line.trim());
		}
		reader.close();
		
		for(int i=0; i < NUMBER_OF_THREADS; i++){
			LoadURLsThread thread = new LoadURLsThread(i+1, REQUEST_DELAY, TOTAL_EXECUTION_TIME, ENV_PATH, urls, WRITE_RESULT_TO_FILE);
			thread.start();
			Thread.sleep(2000);
		}
		
		logger.debug(NUMBER_OF_THREADS + " threads started.");
		logger.debug("***********************************");
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new LoadURLs();
	}
	
	private void initProperties() throws IOException {
		logger.debug("Initiate properties...");
        Properties props = loadProperties(PROPERTY_FILE);   
        
        this.ENV_PATH = props.getProperty("env_path", ENV_PATH);
        this.NUMBER_OF_THREADS = Integer.parseInt(props.getProperty("number_of_threads", String.valueOf(NUMBER_OF_THREADS)));
        this.TOTAL_EXECUTION_TIME = Long.parseLong(props.getProperty("total_execution_time", String.valueOf(TOTAL_EXECUTION_TIME)));
        this.REQUEST_DELAY = Integer.parseInt(props.getProperty("request_delay", String.valueOf(REQUEST_DELAY)));
        this.WRITE_RESULT_TO_FILE = Boolean.parseBoolean(props.getProperty("write_results_to_file", String.valueOf(WRITE_RESULT_TO_FILE)));
        this.URL_INPUT_FILE = props.getProperty("url_input_file", URL_INPUT_FILE);
        
        logger.debug("  env_path               ="+this.ENV_PATH);
        logger.debug("  number_of_threads      ="+this.NUMBER_OF_THREADS);
        logger.debug("  total_execution_time   ="+this.TOTAL_EXECUTION_TIME);
        logger.debug("  request_delay          ="+this.REQUEST_DELAY);
        logger.debug("  write_results_to_file  ="+this.WRITE_RESULT_TO_FILE);
        logger.debug("  url_input_file         ="+this.URL_INPUT_FILE);
	}
	
	private Properties loadProperties(String propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propsFile);
        props.load(fis);    
        fis.close();
        return props;
    }

}
