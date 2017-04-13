package io.thebrother.tusrx.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.RequestHandler;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.rxnetty.TusRxRequestHandler;

public class TestServer {

    private HttpServer<ByteBuf, ByteBuf> server;

    public TestServer() {
        Options options = getOptions();
        RequestHandler<ByteBuf, ByteBuf> requestHandler = new TusRxRequestHandler(
                options);
        server = HttpServer.newServer();

        CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> server.start(requestHandler));
        serverFuture.thenAccept(voyd -> server.awaitShutdown());
        serverFuture.join();
    }

    private static Options getOptions() {
        try {
            String tmpFileStore = System.getenv("TUS_RX_TMP_FILE_STORE");
            Path tmpFilesStorePath = null;
            if (tmpFileStore != null) {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
                tmpFilesStorePath = Files.createTempDirectory("tusFileStore", attr);
            }
            Options options = new Options("files",
                    tmpFilesStorePath != null ? tmpFilesStorePath
                            : Paths.get("/Users/jlefrere/perso/tusRx/tmp"),
                    "http://localhost");
            return options;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        RequestHandler<ByteBuf, ByteBuf> requestHandler = new TusRxRequestHandler(
                getOptions());
        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080).start(requestHandler);

        server.awaitShutdown();
    }

}
