package com.e95mla.tools.loadurls;

import org.apache.log4j.PatternLayout;

public class SpecialPatternLayout extends PatternLayout { 

	 @Override  
	    public String getHeader() {  
	        return "Timestamp|Thread|Time|Bytes|url" + System.getProperty("line.separator"); 
	    }  
	
}
