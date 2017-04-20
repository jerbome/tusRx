package io.thebrother.tusrx.response.impl;

import java.util.ArrayList;
import java.util.Collection;

import io.thebrother.tusrx.http.TusHeader;
import io.thebrother.tusrx.response.TusResponse;

public class TusResponseImpl implements TusResponse {
    
    private final ArrayList<TusHeader> headers = new ArrayList<>();
    
    private final int statusCode;

    public TusResponseImpl(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public static TusResponse noContent() {
        return new TusResponseImpl(204);
    }
    
    public static TusResponse badRequest() {
        return new TusResponseImpl(400);
    }
    
    public static TusResponse notFound() {
        return new TusResponseImpl(404);
    }
    
    public static TusResponse requestEntityTooLarge() {
        return new TusResponseImpl(413);
    }

    public static TusResponse conflict() {
        return new TusResponseImpl(409);
    }
    
    public static TusResponse notImplemented() {
        // TODO Auto-generated method stub
        return new TusResponseImpl(501);
    }

    @Override
    public void setHeader(String header, String value) {
        headers.add(new TusHeader(header, value));

    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Collection<TusHeader> getHeaders() {
        return headers;
    }

    public static TusResponse created() {
       return new TusResponseImpl(201);
    }

}
