package io.thebrother.tusrx.handler;

public interface RequestHandlerFactory<T> {
    RequestHandler makeHandler(T request);
}
