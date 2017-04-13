package io.thebrother.tusrx;

import java.nio.file.Path;
import java.util.Collections;

public class Options {
    
    private final String basePath;
    private final Path rootDir;
    
    public Options(String basePath, Path rootDir) {
        this.basePath = basePath;
        this.rootDir = rootDir;
    }
    
	public String getResumable() {
		return "1.0.0";
	}
	
	public String getVersion() {
		return "1.0.0";
	}
	
	public long getMaxSize() {
		return Integer.MAX_VALUE;
	}
	
	public Iterable<String> getExtensions() {
		return Collections.singleton("creation");
	}
	
	public String getBasePath() {
	    return this.basePath;
	}

    public Path getRootDir() {
        return rootDir;
    }
}
