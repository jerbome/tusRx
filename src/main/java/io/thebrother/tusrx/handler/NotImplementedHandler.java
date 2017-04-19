package io.thebrother.tusrx.handler;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;

import rx.Observable;

public class NotImplementedHandler implements RequestHandler {

    @Override
    public Observable<TusResponse> handle() {
        TusResponse notImplemented = new TusResponseImpl();
        notImplemented.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
        return Observable.just(notImplemented);
    }

}
