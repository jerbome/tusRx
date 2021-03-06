package io.thebrother.tusrx.handler;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.response.impl.TusResponseImpl;
import io.thebrother.tusrx.upload.UploaderPool;

import rx.Observable;

public class HeadHandler extends BaseRequestHandler implements RequestHandler {
    
    private final UploaderPool pool;

    public HeadHandler(Options options, TusRequest request, UploaderPool pool) {
        super(options, request);
        this.pool  = pool;
    }

    @Override
    public Observable<TusResponse> handle() {
        return pool.getUploader(request.getUuid()).map(up -> { 
            TusResponse response = TusResponseImpl.noContent();
            response.setHeader("Tus-Resumable", options.getResumable());
            response.setHeader("Upload-Offset", Long.toString(up.getOffset().get()));
            response.setHeader("Upload-Length", Long.toString(up.getUploadLength()));
            return Observable.just(response);
        }).orElseGet(() -> { 
            TusResponse response = TusResponseImpl.notFound();
            return Observable.just(response);
        });
    }

}
