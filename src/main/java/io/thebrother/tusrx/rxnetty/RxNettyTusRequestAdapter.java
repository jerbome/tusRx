package io.thebrother.tusrx.rxnetty;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import io.thebrother.tusrx.entry.TusRequest;

import rx.Observable;

public class RxNettyTusRequestAdapter implements TusRequest {
    private final HttpServerRequest<ByteBuf> rxNettyRequest;
    
    public RxNettyTusRequestAdapter( HttpServerRequest<ByteBuf> rxNettyRequest) {
        this.rxNettyRequest =  rxNettyRequest;
    }

    @Override
    public Method getMethod() {
        return Method.valueOf(rxNettyRequest.getHttpMethod().name());
    }

    @Override
    public UUID getUuid() {
        String[] splitPath = rxNettyRequest.getDecodedPath().split("/");
        try{
            return UUID.fromString(splitPath[splitPath.length - 1]);
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    @Override
    public Observable<ByteBuffer> getContent() {
        return rxNettyRequest.getContent().map(ByteBuf::nioBuffer);
    }

    @Override
    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(rxNettyRequest.getHeader(name));
    }
    
    
}
