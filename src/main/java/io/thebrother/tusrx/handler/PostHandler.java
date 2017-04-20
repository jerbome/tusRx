package io.thebrother.tusrx.handler;

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
        if (isPostRequestValid(request)) {
            // TODO Optional mixed with Observable is getting harder to read.
            // Use only Observable?
            return request.getHeader("Upload-Length")
                    .map(Long::parseLong)
                    .filter(v -> v <= options.getMaxSize())
                    .map(v -> {
                        return pool.newUploader(v)
                                .map(uuid -> {
                                    TusResponse response = TusResponseImpl.created();
                                    response.setHeader("Tus-Resumable", options.getResumable());
                                    response.setHeader("Location",
                                            options.getHostUrl() + "/" + options.getBasePath() + "/" + uuid.toString());
                                    return response;
                                });
                    }).orElseGet(() -> {
                        return Observable.just(TusResponseImpl.requestEntityTooLarge());
                    });
        } else {
            return Observable.just(TusResponseImpl.badRequest());
        }
    }

    private boolean isPostRequestValid(TusRequest request) {
        return request.getHeader("Tus-Resumable").map(v -> v.equals(options.getResumable())).orElse(false) &&
                request.getHeader("Upload-Length").map(s -> true).orElse(false);
    }

}
