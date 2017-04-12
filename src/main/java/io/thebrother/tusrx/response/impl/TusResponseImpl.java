package io.thebrother.tusrx.response.impl;

import java.util.ArrayList;
import java.util.Collection;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.http.TusHeader;
import io.thebrother.tusrx.response.TusResponse;

public class TusResponseImpl implements TusResponse {
    
    private final ArrayList<TusHeader> headers = new ArrayList<>();
    private HttpResponseStatus status; //FIXME don't use rxnetty object

    @Override
    public void setHeader(String header, String value) {
        headers.add(new TusHeader(header, value));

    }

    @Override
    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    @Override
    public HttpResponseStatus getStatus() {
        return this.status;
    }

    @Override
    public Collection<TusHeader> getHeaders() {
        return headers;
    }

}
