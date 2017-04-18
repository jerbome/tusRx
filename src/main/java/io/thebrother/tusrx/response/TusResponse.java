package io.thebrother.tusrx.response;

import java.util.Collection;
import java.util.Collections;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.http.TusHeader;

public interface TusResponse {

    TusResponse NOT_FOUND = new TusResponse() {

        @Override
        public void setStatus(HttpResponseStatus noContent) {
        }

        @Override
        public HttpResponseStatus getStatus() {
            return HttpResponseStatus.NOT_FOUND;
        }

        @Override
        public Collection<TusHeader> getHeaders() {
            return Collections.emptyList();
        }

        @Override
        public void setHeader(String header, String value) {
        }
        
    };
    TusResponse CONFLICT = new TusResponse() {

        @Override
        public void setStatus(HttpResponseStatus noContent) {
        }

        @Override
        public HttpResponseStatus getStatus() {
            return HttpResponseStatus.CONFLICT;
        }

        @Override
        public Collection<TusHeader> getHeaders() {
            return Collections.emptyList();
        }

        @Override
        public void setHeader(String header, String value) {
        }
        
    };
    TusResponse BAD_REQUEST = new TusResponse() {

        @Override
        public void setStatus(HttpResponseStatus noContent) {
        }

        @Override
        public HttpResponseStatus getStatus() {
            return HttpResponseStatus.BAD_REQUEST;
        }

        @Override
        public Collection<TusHeader> getHeaders() {
            return Collections.emptyList();
        }

        @Override
        public void setHeader(String header, String value) {
        }
        
    };

    void setStatus(HttpResponseStatus noContent);

    HttpResponseStatus getStatus();
    
    Collection<TusHeader> getHeaders();

    void setHeader(String header, String value);
}
