package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import io.thebrother.tusrx.entry.TusRequest.Method;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class OptionHandlerTest extends RequestHandlerTest {

    private final String tusResumable = "1.0.0";
    private final List<String> extensions = Arrays.asList("extension1", "extension2");

    @Before
    public void initOptions() {
        when(request.getMethod()).thenReturn(Method.OPTIONS);

        when(options.getExtensions()).thenReturn(extensions);
        when(options.getResumable()).thenReturn(tusResumable);
        when(options.getVersion()).thenReturn(tusResumable);
        handler = new OptionHandler(options, request);
    }

    @Test
    public void testOptionsResponds204() {
        // arrange

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            assertThat(t.getStatusCode()).isEqualTo(204);
        });
    }

    @Test
    public void testOptionsSetTusResumableHeader() {
        // arrange

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(t);
            assertThat(responseAssert).hasHeader("Tus-Resumable", tusResumable);
        });
    }

    @Test
    public void testOptionsSetTusVersionHeader() {
        // arrange

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(t);
            assertThat(responseAssert).hasHeader("Tus-Version", tusResumable);
        });
    }

    @Test
    public void testOptionsSetTusExtensionHeader() {
        // arrange

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert reponseAssert = new ResponseHeaderAssert(t);
            assertThat(reponseAssert).hasHeader("Tus-Extension", String.join(",", extensions));
        });
    }
    
    @Test
    public void testOptionsDoesNotSetTusExtensionHeaderWhenExtensionsIsNull() {
     // arrange
        when(options.getExtensions()).thenReturn(null);
        
        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert reponseAssert = new ResponseHeaderAssert(t);
            assertThat(reponseAssert).doesNotHaveHeader("Tus-Extension");
        });
    }
    
    @Test
    public void testOptionsDoesNotSetTusExtensionHeaderWhenExtensionsIsEmpty() {
     // arrange
        when(options.getExtensions()).thenReturn(Collections.emptyList());
        
        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert reponseAssert = new ResponseHeaderAssert(t);
            assertThat(reponseAssert).doesNotHaveHeader("Tus-Extension");
        });
    }
}
