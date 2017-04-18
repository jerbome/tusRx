package io.thebrother.tusrx.http;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

public class HttpPostTest extends BaseHttpTest {

    @Test
    public void testStatus() {
        Iterable<HttpClientResponse<ByteBuf>> response = serverRule.getHttpClient()
                .createPost("/files")
                .addHeader("Tus-Resumable", "1.0.0")
                .addHeader("Upload-Length", "100")
                .toBlocking()
                .toIterable();
        Iterator<HttpClientResponse<ByteBuf>> it = response.iterator();
        assertTrue(it.hasNext());
        assertEquals(HttpResponseStatus.CREATED, it.next().getStatus());
    }

    @Test
    public void testLocation() {
        Iterable<HttpClientResponse<ByteBuf>> response = serverRule.getHttpClient().createPost("/files")
                .addHeader("Tus-Resumable", "1.0.0")
                .addHeader("Upload-Length", "100")
                .toBlocking()
                .toIterable();
        Iterator<HttpClientResponse<ByteBuf>> it = response.iterator();
        assertTrue(it.hasNext());
        assertNotNull(it.next().getHeader("Location"));
    }

}
