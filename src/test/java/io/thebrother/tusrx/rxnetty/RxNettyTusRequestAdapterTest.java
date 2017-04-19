package io.thebrother.tusrx.rxnetty;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import io.thebrother.tusrx.entry.TusRequest;

import rx.Observable;

public class RxNettyTusRequestAdapterTest {

    @Mock
    private HttpServerRequest<ByteBuf> req;

    private final Observable<HttpMethod> supportedMethods = Observable.just(HttpMethod.GET, HttpMethod.POST,
            HttpMethod.PATCH, HttpMethod.OPTIONS, HttpMethod.HEAD, HttpMethod.DELETE);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSupportedMethods() {
        final List<Throwable> t = new ArrayList<>();
        supportedMethods.doOnNext(hm -> when(req.getHttpMethod()).thenReturn(hm))
            .map(hm -> new RxNettyTusRequestAdapter(req))
            .map(TusRequest::getMethod)
            .zipWith(supportedMethods, (expected, actual) -> expected.equals(actual))
            .subscribe(b -> {}, x -> t.add(x), () -> {});

        assertThat(t).isEmpty();
    }

}
