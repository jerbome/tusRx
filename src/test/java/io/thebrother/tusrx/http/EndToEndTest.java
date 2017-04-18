package io.thebrother.tusrx.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import rx.Observable;

public class EndToEndTest extends BaseHttpTest {
    
    @Test
    public void testTwoPartsUpload() {
        serverRule.getHttpClient().createPost("/files")
                .addHeader("Tus-Resumable", "1.0.0")
                .addHeader("Upload-Length", 100L)
                .map(r -> r.getHeader("Location"))
                .filter(Objects::nonNull)
                .flatMap(loc -> serverRule.getHttpClient().createPatch(loc)
                        .setHeader("Upload-Offset", 0L)
                        .writeStringContent(
                                Observable.just("hello", " ", "world", "\n")
                                        .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS).startWith(0L),
                                                (str, nada) -> str),
                                str -> true)
                        .map(resp -> resp.getHeader("Upload-Offset"))
                        .concatMap(offset -> {
                            return serverRule.getHttpClient().createPatch(loc)
                                    .setHeader("Upload-Offset", offset)
                                    .writeStringContent(
                                            Observable.just("goodbye", " ", "hell", "\n")
                                                    .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS)
                                                            .startWith(0L),
                                                            (str, nada) -> str),
                                            str -> true);
                        }))
                .toBlocking()
                .subscribe();
    }

    @Test
    public void testConcurrentPatch() throws InterruptedException {
        HttpClientRequest<ByteBuf, ByteBuf> post = serverRule.getHttpClient().createPost("/files")
                .addHeader("Tus-Resumable", "1.0.0")
                .addHeader("Upload-Length", 100L);
        Observable<byte[]> slowContent = Observable.just("hello ".getBytes()).repeat().zipWith(Observable.interval(50, TimeUnit.MILLISECONDS).startWith(0L), (data, nop)  -> data).take(10);
        Observable<byte[]> fastContent = Observable.just("goodbye ".getBytes()).repeat().zipWith(Observable.interval(10, TimeUnit.MILLISECONDS).startWith(0L), (data, nop)  -> data).take(10);

        Iterator<HttpClientResponse<ByteBuf>> iterator = post.map(r -> r.getHeader("Location"))
                .flatMap(location -> Observable.merge(
                        patch(location, slowContent),
                        patch(location, fastContent).delay(120, TimeUnit.MILLISECONDS)))
                .toBlocking().getIterator();
        
        // the first response should be the failure
        assertThat(iterator.next()).isNotNull()
            .extracting(HttpClientResponse::getStatus).containsExactly(HttpResponseStatus.BAD_REQUEST);
        
        // the second one should be sucessfull
        assertThat(iterator.next()).isNotNull()
            .extracting(HttpClientResponse::getStatus).containsExactly(HttpResponseStatus.NO_CONTENT);
        

    }

    private Observable<HttpClientResponse<ByteBuf>> patch(String location, Observable<byte[]> contents) {
        return serverRule.getHttpClient().createPatch(location).setHeader("Upload-Offset", 0L)
                .writeBytesContent(contents, arr -> true);
    }
}
