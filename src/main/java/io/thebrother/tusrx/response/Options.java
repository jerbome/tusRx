package io.thebrother.tusrx.response;

import java.util.Collection;
import java.util.Collections;

public class Options {
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
}
