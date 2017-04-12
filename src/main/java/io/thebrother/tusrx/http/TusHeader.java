package io.thebrother.tusrx.http;

public class TusHeader {
    private final String name;
    private final String value;
    
    
    public TusHeader(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }
    
}
