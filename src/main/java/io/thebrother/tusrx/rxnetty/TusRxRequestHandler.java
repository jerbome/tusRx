package io.thebrother.tusrx.rxnetty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.*;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.handler.RequestHandlerFactory;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class TusRxRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {
    
	private final Options options;
	
	private final RequestHandlerFactory<HttpServerRequest<ByteBuf>> handlerFactory;
	
	public TusRxRequestHandler(Options options, RequestHandlerFactory<HttpServerRequest<ByteBuf>> handlerFactory) {
	    this.options = options;
	    this.handlerFactory = handlerFactory;
    }
	
	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
	    String path = request.getDecodedPath().substring(1);
	    String[] pathArray = path.split("/");
	    
	    if (pathArray.length > 0 && options.getBasePath().equals(pathArray[0])) {
	        Observable<TusResponse> tResp = handlerFactory.makeHandler(request).handle();
	        return tResp.map(tr -> { 
	            tr.getHeaders().stream()
	            .forEach(h -> response.setHeader(h.getName(), h.getValue()));
	            response.setStatus(HttpResponseStatus.valueOf(tr.getStatusCode()));
	            return null;
	        });
	    }
		return http404(response);
	}

	private Observable<Void> http404(HttpServerResponse<ByteBuf> response) {
        return response.setStatus(HttpResponseStatus.NOT_FOUND);
    }

}
