package io.thebrother.tusrx;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;
import io.thebrother.tusrx.upload.ChunkAlreadyUploadingException;
import io.thebrother.tusrx.upload.UploaderPool;

import rx.Observable;

public class TusRx {

    private static final Logger logger = LoggerFactory.getLogger(TusRx.class);

    private static final Observable<TusResponse> NOT_FOUND = Observable.just(TusResponse.NOT_FOUND);

    private final UploaderPool pool;

    private final Options options;

    public TusRx(Options options) {
        this(options, new UploaderPool(options.getRootDir()));
    }

    public TusRx(Options options, UploaderPool pool) {
        this.options = options;
        this.pool = pool;
    }

    public Observable<TusResponse> handle(TusRequest request) {
        logger.debug("Handling " + request);
        switch (request.getMethod()) {
        case DELETE:
            break;
        case GET:
            break;
        case HEAD:
            return handleHead(request);
        case OPTIONS:
            return handleOption(request);
        case PATCH:
            return handlePatch(request);
        case POST:
            return handlePost(request);
        default:
            break;

        }
        return NOT_FOUND;
    }

    private Observable<TusResponse> handleHead(TusRequest request) {
        return pool.getUploader(request.getUuid()).map(up -> { 
            TusResponse response = new TusResponseImpl();
            response.setStatus(HttpResponseStatus.NO_CONTENT);
            response.setHeader("Tus-Resumable", options.getResumable());
            response.setHeader("Upload-Offset", Long.toString(up.getOffset().get()));
            response.setHeader("Upload-Length", Long.toString(up.getUploadLength()));
            return Observable.just(response);
        }).orElse(NOT_FOUND);
    }

    private Observable<TusResponse> handlePatch(TusRequest request) {
        return pool.getUploader(request.getUuid()).map(up -> {
            AtomicLong offset = up.getOffset();
            return request.getHeader("Upload-Offset")
                    .map(Long::parseLong)
                    .map(reqOffset -> {
                        if (reqOffset == offset.get()) {
                            Observable<Long> bytesUploaded = up.uploadChunk(request);
                            return bytesUploaded
                                    .doOnError(x -> logger.error("something went awry when copying PATCH content", x))
                                    .onErrorResumeNext(x -> {
                                        if (x instanceof ChunkAlreadyUploadingException) {
                                            return Observable.error(x);
                                        } else {
                                            return Observable.empty();
                                        }
                                    })
                                    .reduce(0L, (a, b) -> a + b)
                                    .map(l -> {
                                        TusResponse tusResponse = new TusResponseImpl();
                                        tusResponse.setStatus(HttpResponseStatus.NO_CONTENT);
                                        tusResponse.setHeader("Tus-Resumable", options.getResumable());
                                        tusResponse.setHeader("Upload-Offset", Long.toString(offset.addAndGet(l)));
                                        return tusResponse;
                                    })
                                    .onErrorResumeNext(x-> Observable.just(TusResponse.BAD_REQUEST))
                                    .doAfterTerminate(() -> {});
                        } else {
                            return Observable.just(TusResponse.CONFLICT);
                        }
                    }).orElse(Observable.just(TusResponse.BAD_REQUEST));
        }).orElse(Observable.just(TusResponse.NOT_FOUND));

    }

    private Observable<TusResponse> handlePost(TusRequest request) {
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

    private Observable<TusResponse> handleOption(TusRequest request) {
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
