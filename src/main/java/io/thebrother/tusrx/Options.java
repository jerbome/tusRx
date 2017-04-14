package io.thebrother.tusrx;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class Options {
    
    private final String basePath;
    private final Path rootDir;
    private final String hostUrl;
    
    public Options(String basePath, Path rootDir, String hostUrl) {
        this.basePath = basePath;
        this.rootDir = rootDir;
        this.hostUrl = hostUrl;
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
	
	public Collection<String> getExtensions() {
		return Collections.singleton("creation");
	}
	
	public String getBasePath() {
	    return this.basePath;
	}

    public Path getRootDir() {
        return this.rootDir;
    }
    
    public String getHostUrl() {
        return this.hostUrl;
    }
}
