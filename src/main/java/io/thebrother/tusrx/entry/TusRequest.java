package io.thebrother.tusrx.entry;

import java.nio.ByteBuffer;
import java.util.*;

import rx.Observable;

public interface TusRequest {
    
    Method getMethod();
    
    Optional<String> getHeader(String name);
    
    Observable<ByteBuffer> getContent();
    
    public enum Method {
        GET, POST, HEAD, OPTIONS, DELETE, PATCH;
    }

    UUID getUuid();
}
