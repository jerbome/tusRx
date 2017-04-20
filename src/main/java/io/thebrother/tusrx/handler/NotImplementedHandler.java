package io.thebrother.tusrx.handler;

import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;

import rx.Observable;

public class NotImplementedHandler implements RequestHandler {

    @Override
    public Observable<TusResponse> handle() {
        TusResponse notImplemented = TusResponseImpl.notImplemented();
        return Observable.just(notImplemented);
    }

}
