package io.thebrother.tusrx.rxnetty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.handler.*;
import io.thebrother.tusrx.upload.UploaderPool;

public class RxNettyRequestHandlerFactory implements RequestHandlerFactory<HttpServerRequest<ByteBuf>> {

    private final Options options;
    private final UploaderPool pool;
    
    public RxNettyRequestHandlerFactory(Options options, UploaderPool pool) {
        this.options = options;
        this.pool = pool;
    }
    
    @Override
    public RequestHandler makeHandler(HttpServerRequest<ByteBuf> request) {
        HttpMethod method = request.getHttpMethod();
        TusRequest tusRequest = new RxNettyTusRequestAdapter(request);
        if (method.equals(HttpMethod.OPTIONS)) {
            return new OptionHandler(options, tusRequest);
        }
        if (method.equals(HttpMethod.POST)) {
            return new PostHandler(options, tusRequest, pool);
        }
        if (method.equals(HttpMethod.HEAD)) {
            return new HeadHandler(options, tusRequest, pool);
        }
        if (method.equals(HttpMethod.PATCH)) {
            return new PatchHandler(options, tusRequest, pool);
        }
        return new NotImplementedHandler();
    }

}
