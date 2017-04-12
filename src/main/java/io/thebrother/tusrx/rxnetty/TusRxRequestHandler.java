package io.thebrother.tusrx.rxnetty;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.*;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.TusRx;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class TusRxRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {
    
	private final Options options;
	private final TusRx tusRx;
	
	public TusRxRequestHandler(Options options) {
	    this.options = options;
	    this.tusRx = new TusRx(options);
    }
	
	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
	    String path = request.getDecodedPath().substring(1);
	    String[] pathArray = path.split("/");
	    
	    if (pathArray.length > 0 && options.getBasePath().equals(pathArray[0])) {
	        Observable<TusResponse> tResp = tusRx.handle(new RxNettyTusRequestAdapter(request, pathArray.length ==2 ? UUID.fromString(pathArray[1]) : null));
	        return tResp.<Void>map((TusResponse tr) -> { 
	            tr.getHeaders().stream()
	            .forEach(h -> response.setHeader(h.getName(), h.getValue()));
	            response.setStatus(tr.getStatus());
	            return null;
	        });
	    }
		return http404(response);
	}

	private Observable<Void> http404(HttpServerResponse<ByteBuf> response) {
        return response.setStatus(HttpResponseStatus.NOT_FOUND);
    }

}
