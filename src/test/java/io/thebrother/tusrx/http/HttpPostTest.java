package io.thebrother.tusrx.http;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import rx.Observable;

public class HttpPostTest extends BaseHttpTest {

    @Test
    public void testStatus() {
        Iterable<HttpClientResponse<ByteBuf>> response = serverRule.getHttpClient().createPost("/files").toBlocking()
                .toIterable();
        Iterator<HttpClientResponse<ByteBuf>> it = response.iterator();
        assertTrue(it.hasNext());
        assertEquals(HttpResponseStatus.CREATED, it.next().getStatus());
    }

    @Test
    public void testLocation() {
        Iterable<HttpClientResponse<ByteBuf>> response = serverRule.getHttpClient().createPost("/files").toBlocking()
                .toIterable();
        Iterator<HttpClientResponse<ByteBuf>> it = response.iterator();
        assertTrue(it.hasNext());
        assertNotNull(it.next().getHeader("Location"));
    }

    @Test
    public void testStuff() {
        serverRule.getHttpClient().createPost("/files")
                .addHeader("Tus-Resumable", "1.0.0")
                .addHeader("Upload-Length", 100L)
                .map(r -> r.getHeader("Location"))
                .filter(Objects::nonNull)
                .flatMap(loc -> serverRule.getHttpClient().createPatch(loc)
                        .writeStringContent(
                                Observable.just("hello", " ", "world", "\n")
                                        .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS).startWith(0L),
                                                (str, nada) -> str),
                                // .delay(25, TimeUnit.MILLISECONDS)
                                // .repeat(1000),
                                str -> true)
                        .concatWith(serverRule.getHttpClient().createPatch(loc).writeStringContent(
                                Observable.just("goodbye", " ", "hell", "\n")
                                        .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS).startWith(0L),
                                                (str, nada) -> str),
                                // .delay(25, TimeUnit.MILLISECONDS)
                                // .repeat(1000),
                                str -> true)))
                .toBlocking()
                .subscribe();
    }
}
