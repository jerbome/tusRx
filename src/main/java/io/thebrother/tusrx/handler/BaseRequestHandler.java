package io.thebrother.tusrx.handler;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;

public abstract class BaseRequestHandler implements RequestHandler {
    protected final Options options;
    protected final TusRequest request;
    
    protected BaseRequestHandler(Options options, TusRequest request) {
        this.options = options;
        this.request = request;
    }
}
