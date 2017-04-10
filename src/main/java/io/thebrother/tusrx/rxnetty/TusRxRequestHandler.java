package io.thebrother.tusrx.rxnetty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import io.thebrother.tusrx.response.Options;
import rx.Observable;

public class TusRxRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {

	private final Options options = new Options();
	
	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
		if (request.getHttpMethod().equals(HttpMethod.OPTIONS)) {
			return handleOptions(response);
		}
		return response.writeStringAndFlushOnEach(Observable.just("yes"));
	}

	private Observable<Void> handleOptions(HttpServerResponse<ByteBuf> response) {
	    response.setStatus(HttpResponseStatus.NO_CONTENT);
		response.setHeader("Tus-Resumable", options.getResumable());
		response.setHeader("Tus-Version", options.getVersion());
		response.setHeader("Tus-Max-Size", options.getMaxSize());
		if (options.getExtensions() != null) {
		    response.setHeader("Tus-Extension", String.join(",", options.getExtensions()));
		}
		return Observable.empty();
	}

}
