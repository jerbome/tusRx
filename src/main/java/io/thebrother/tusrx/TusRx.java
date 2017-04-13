package io.thebrother.tusrx;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;
import io.thebrother.tusrx.upload.UploaderPool;

import rx.Observable;

public class TusRx {

    private static final Logger logger = LoggerFactory.getLogger(TusRx.class);

    private final UploaderPool pool;

    private final Options options;

    public TusRx(Options options) {
        this.options = options;
        this.pool = new UploaderPool(options.getRootDir());
    }

    public Observable<TusResponse> handle(TusRequest request) {
        logger.debug("Handling " + request);
        switch (request.getMethod()) {
        case DELETE:
            break;
        case GET:
            break;
        case HEAD:
            break;
        case OPTIONS:
            return handleOption(request);
        case PATCH:
            return handlePatch(request);
        case POST:
            return handlePost(request);
        default:
            break;

        }
        return null;
    }

    private Observable<TusResponse> handlePatch(TusRequest request) {
        return pool.getUploader(request.getUuid()).map(up -> {
            Observable<Long> bytesUploaded = up.uploadChunk(request);
            return bytesUploaded
                    .reduce((a, b) -> a + b)
                    .map(l -> {
                        TusResponse tusResponse = new TusResponseImpl();
                        tusResponse.setStatus(HttpResponseStatus.NO_CONTENT);
                        return tusResponse;
                    });
        }).orElse(Observable.just(TusResponse.NOT_FOUND));

    }

    private Observable<TusResponse> handlePost(TusRequest request) {
        TusResponse response = new TusResponseImpl();
        if (isRequestValid(request)) {
            // TODO Optional mixed with Observable is getting harder to read. Use only Observable
            return request.getHeader("Upload-Length")
                    .filter(v -> Long.parseLong(v) > options.getMaxSize())
                    .map(v -> {
                        response.setStatus(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
                        return Observable.just(response);
                    }).orElseGet(() -> {
                        return pool.newUploader()
                                .map(uuid -> {
                                    response.setStatus(HttpResponseStatus.CREATED);
                                    response.setHeader("Location", "/" + options.getBasePath() + "/" + uuid.toString());
                                    return response;
                                });
                    });
        } else {
            response.setStatus(HttpResponseStatus.BAD_REQUEST);
            return Observable.just(response);
        }
    }

    private boolean isRequestValid(TusRequest request) {
        return request.getHeader("Tus-Resumable").map(v -> v.equals(options.getResumable())).orElse(false) &&
                request.getHeader("Upload-Length").map(s -> true).orElse(false);
    }

    private Observable<TusResponse> handleOption(TusRequest request) {
        TusResponse response = new TusResponseImpl();
        response.setStatus(HttpResponseStatus.NO_CONTENT);
        response.setHeader("Tus-Resumable", options.getResumable());
        response.setHeader("Tus-Version", options.getVersion());
        response.setHeader("Tus-Max-Size", Long.toString(options.getMaxSize()));
        if (options.getExtensions() != null) {
            response.setHeader("Tus-Extension", String.join(",", options.getExtensions()));
        }
        return Observable.just(response);
    }

}
