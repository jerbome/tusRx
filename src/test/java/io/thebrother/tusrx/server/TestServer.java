package io.thebrother.tusrx.server;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import io.thebrother.tusrx.rxnetty.TusRxRequestHandler;

public class TestServer {

    private HttpServer<ByteBuf, ByteBuf> server;

    public TestServer() {
        RequestHandler<ByteBuf, ByteBuf> requestHandler = new TusRxRequestHandler();
        server = HttpServer.newServer();
        
        CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> server.start(requestHandler));
        serverFuture.thenAccept(voyd -> server.awaitShutdown());
        serverFuture.join();
    }
    
    
    public SocketAddress getServerAddress() {
        return server.getServerAddress();
    }


    public int getServerPort() {
        return server.getServerPort();
    }
    
    public void shutdown() {
        server.shutdown();
    }

    public static void main(String args[]) {
        RequestHandler<ByteBuf, ByteBuf> requestHandler = new TusRxRequestHandler();
        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080).start(requestHandler);

        server.awaitShutdown();
    }
    
    

}
