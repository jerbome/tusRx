package io.thebrother.tusrx.options;


import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.thebrother.tusrx.server.TestServerRule;

public class HttpOptionsTest {

    private static Logger logger = LoggerFactory.getLogger(HttpOptionsTest.class);
    
    @ClassRule public static TestServerRule serverRule = new TestServerRule(true);

    @Test
    public void testTusResumable() {
        HttpClientRequest<ByteBuf, ByteBuf> request = serverRule.getHttpClient().createOptions("/files");
        request.flatMap(HttpClientResponse::discardContent).ignoreElements();
        request.subscribe(resp -> logger.info(resp.toString()), x -> logger.error(x.toString()), ()-> logger.info("done"));
        request
            .doOnNext(resp-> logger.info(resp.toString()))
            .map(resp -> resp.getHeader("Tus-Resumable"))
            .toBlocking()
            .forEach(hv -> assertEquals("1.0.0", hv));
    }
    
    @Test
    public void testTusVersion() {
        serverRule.getHttpClient().createOptions("/files")
        .doOnNext(resp-> logger.info(resp.toString()))
        .map(resp -> resp.getHeader("Tus-Version"))
        .toBlocking()
        .forEach(hv -> assertEquals("1.0.0", hv));
    }
    
    @Test
    public void testTusMaxSize() {
        serverRule.getHttpClient().createOptions("/files")
        .doOnNext(resp-> logger.info(resp.toString()))
        .map(resp -> resp.getHeader("Tus-Max-Size"))
        .toBlocking()
        .forEach(hv -> assertEquals(Integer.MAX_VALUE, Integer.parseInt(hv)));
    }
    
    @Test
    public void testTusExtensions() {
        serverRule.getHttpClient().createOptions("/files")
        .doOnNext(resp-> logger.info(resp.toString()))
        .map(resp -> resp.getHeader("Tus-Extension"))
        .toBlocking()
        .forEach(hv -> assertEquals("creation", hv));
    }
    
    @Test
    public void testOptionsStatusCode() {
        serverRule.getHttpClient().createOptions("/files")
        .doOnNext(resp-> logger.info(resp.toString()))
        .map(resp -> resp.getStatus())
        .toBlocking()
        .forEach(status -> assertEquals(HttpResponseStatus.NO_CONTENT, status));
    }
    
}
