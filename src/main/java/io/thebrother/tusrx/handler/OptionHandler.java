package io.thebrother.tusrx.handler;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;

import rx.Observable;

public class OptionHandler extends BaseRequestHandler {

    public OptionHandler(Options options, TusRequest request) {
        super(options, request);
    }

    @Override
    public Observable<TusResponse> handle() {
        TusResponse response = new TusResponseImpl();
        response.setStatus(HttpResponseStatus.NO_CONTENT);
        response.setHeader("Tus-Resumable", options.getResumable());
        response.setHeader("Tus-Version", options.getVersion());
        response.setHeader("Tus-Max-Size", Long.toString(options.getMaxSize()));
        if (options.getExtensions() != null && options.getExtensions().size() > 0) {
            response.setHeader("Tus-Extension", String.join(",", options.getExtensions()));
        }
        return Observable.just(response);
    }

}
