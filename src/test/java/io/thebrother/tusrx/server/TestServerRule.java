package io.thebrother.tusrx.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.rules.ExternalResource;

import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;

public class TestServerRule extends ExternalResource {

    private TestServer server;
    private final boolean embedded;
    
    public TestServerRule() {
        this(true);
    }
    
    public TestServerRule(boolean embedded) {
        this.embedded = embedded;
    }
    
    private HttpClient<ByteBuf, ByteBuf> client;

    @Override
    protected void before() throws Throwable {
        super.before();
        if (embedded) {
            server = new TestServer();
        }
    }

    @Override
    protected void after() {
        if (embedded) {
            server.shutdown();
        }
        super.after();
    }
    
    public SocketAddress getServerAddress() {
        return embedded ? server.getServerAddress() : null;
    }

    public int getPort() {
        return embedded ? server.getServerPort() : 8080;
    }
    
    public HttpClient<ByteBuf, ByteBuf> getHttpClient () {
        if (client == null) {
            HttpClient<ByteBuf, ByteBuf> _client = embedded ? HttpClient.newClient(getServerAddress()) :  HttpClient.newClient(new InetSocketAddress("localhost", 8080));
            client = _client.enableWireLogging("client", LogLevel.ERROR);
        }
        return client;
    }

}
