package io.thebrother.tusrx.response;

public interface ResponseAdapter<R> {
    void adapt(TusResponse tResp, R response);
}
