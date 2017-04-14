package io.thebrother.tusrx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.internal.Failures;
import org.junit.Before;
import org.junit.Test;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.entry.TusRequest.Method;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class TusRxOptionsTest extends TusRxTest {

    private final String tusResumable = "1.0.0";
    private final List<String> extensions = Arrays.asList("extension1", "extension2");

    @Before
    public void initOptions() {
        when(request.getMethod()).thenReturn(Method.OPTIONS);

        when(options.getExtensions()).thenReturn(extensions);
        when(options.getResumable()).thenReturn(tusResumable);
        when(options.getVersion()).thenReturn(tusResumable);
    }

    @Test
    public void testOptionsResponds204() {
        // arrange

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.NO_CONTENT);
        });
    }

    @Test
    public void testOptionsSetTusResumableHeader() {
        // arrange

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            t.getHeaders().stream().filter(h -> h.getName().equals("Tus-Resumable")).findFirst()
                .map(h -> h.getValue())
                .map(value -> assertThat(value).isEqualTo(tusResumable))
                .orElseThrow(() -> Failures.instance().failure("No Tus-Resumable header set on response"));
        });
    }
    
    @Test
    public void testOptionsSetTusVersionHeader() {
        // arrange

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            t.getHeaders().stream().filter(h -> h.getName().equals("Tus-Version")).findFirst()
                .map(h -> h.getValue())
                .map(value -> assertThat(value).isEqualTo(tusResumable))
                .orElseThrow(() -> Failures.instance().failure("No Tus-Version header set on response"));
        });
    }
    
    @Test
    public void testOptionsSetTusExtensionHeader() {
     // arrange

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert reponseAssert = new ResponseHeaderAssert(t);
            assertThat(reponseAssert).hasHeader("Tus-Extension", String.join(",", extensions));
        });
    }
}
