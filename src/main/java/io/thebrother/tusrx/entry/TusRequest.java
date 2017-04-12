package io.thebrother.tusrx.entry;

import java.nio.ByteBuffer;
import java.util.UUID;

import io.thebrother.tusrx.http.TusHeader;

import rx.Observable;

public interface TusRequest {
    
    Method getMethod();
    
    Observable<TusHeader> getHeaders();
    
    Observable<ByteBuffer> getContent();
    
    public enum Method {
        GET, POST, HEAD, OPTIONS, DELETE, PATCH;
    }

    UUID getUuid();
}
