package io.thebrother.tusrx.handler;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;
import io.thebrother.tusrx.upload.UploaderPool;

import rx.Observable;

public class PostHandler extends BaseRequestHandler {

    private final UploaderPool pool;

    public PostHandler(Options options, TusRequest request, UploaderPool pool) {
        super(options, request);
        this.pool = pool;
    }

    @Override
    public Observable<TusResponse> handle() {
        TusResponse response = new TusResponseImpl();
        if (isPostRequestValid(request)) {
            // TODO Optional mixed with Observable is getting harder to read.
            // Use only Observable?
            return request.getHeader("Upload-Length")
                    .map(Long::parseLong)
                    .filter(v -> v <= options.getMaxSize())
                    .map(v -> {
                        return pool.newUploader(v)
                                .map(uuid -> {
                                    response.setStatus(HttpResponseStatus.CREATED);
                                    response.setHeader("Tus-Resumable", options.getResumable());
                                    response.setHeader("Location",
                                            options.getHostUrl() + "/" + options.getBasePath() + "/" + uuid.toString());
                                    return response;
                                });
                    }).orElseGet(() -> {
                        response.setStatus(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
                        return Observable.just(response);
                    });
        } else {
            response.setStatus(HttpResponseStatus.BAD_REQUEST);
            return Observable.just(response);
        }
    }

    private boolean isPostRequestValid(TusRequest request) {
        return request.getHeader("Tus-Resumable").map(v -> v.equals(options.getResumable())).orElse(false) &&
                request.getHeader("Upload-Length").map(s -> true).orElse(false);
    }

}
