package io.thebrother.tusrx.handler;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;
import io.thebrother.tusrx.upload.ChunkAlreadyUploadingException;
import io.thebrother.tusrx.upload.UploaderPool;

import rx.Observable;

public class PatchHandler extends BaseRequestHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PatchHandler.class);
    
    private final UploaderPool pool;

    public PatchHandler(Options options, TusRequest request, UploaderPool pool) {
        super(options, request);
        this.pool = pool;
    }

    @Override
    public Observable<TusResponse> handle() {
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
                                        TusResponse tusResponse = TusResponseImpl.noContent();
                                        tusResponse.setHeader("Tus-Resumable", options.getResumable());
                                        tusResponse.setHeader("Upload-Offset", Long.toString(offset.addAndGet(l)));
                                        return tusResponse;
                                    })
                                    .onErrorResumeNext(x-> Observable.just(TusResponseImpl.badRequest()))
                                    .doAfterTerminate(() -> {});
                        } else {
                            return Observable.just(TusResponseImpl.conflict());
                        }
                    }).orElse(Observable.just(TusResponseImpl.badRequest()));
        }).orElse(Observable.just(TusResponseImpl.notFound()));
    }

}
