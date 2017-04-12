package io.thebrother.tusrx;

import java.util.Collections;

public class Options {
    
    private final String basePath;
    
    public Options(String basePath) {
        this.basePath = basePath;
    }
    
	public String getResumable() {
		return "1.0.0";
	}
	
	public String getVersion() {
		return "1.0.0";
	}
	
	public int getMaxSize() {
		return Integer.MAX_VALUE;
	}
	
	public Iterable<String> getExtensions() {
		return Collections.singleton("creation");
	}
	
	public String getBasePath() {
	    return this.basePath;
	}
}
