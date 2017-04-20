package io.thebrother.tusrx.response;

import java.util.Collection;

import io.thebrother.tusrx.http.TusHeader;

public interface TusResponse {

    Collection<TusHeader> getHeaders();

    void setHeader(String header, String value);

    int getStatusCode();
}
