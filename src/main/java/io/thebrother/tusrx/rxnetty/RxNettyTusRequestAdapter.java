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
    private final UUID uuid;
    
    public RxNettyTusRequestAdapter( HttpServerRequest<ByteBuf> rxNettyRequest, UUID uuid) {
        this.rxNettyRequest =  rxNettyRequest;
        this.uuid = uuid;
    }

    @Override
    public Method getMethod() {
        return Method.valueOf(rxNettyRequest.getHttpMethod().name());
    }

    @Override
    public UUID getUuid() {
        return uuid;
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
