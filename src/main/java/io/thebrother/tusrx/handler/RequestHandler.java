package io.thebrother.tusrx.handler;

import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public interface RequestHandler {
    Observable<TusResponse> handle();
}
