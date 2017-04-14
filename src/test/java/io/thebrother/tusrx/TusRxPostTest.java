package io.thebrother.tusrx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.internal.Failures;
import org.junit.Before;
import org.junit.Test;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.entry.TusRequest.Method;
import io.thebrother.tusrx.http.TusHeader;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class TusRxPostTest extends TusRxTest {

    private final String tusResumable = "resumableHeader";
    private final long maxSize = 100L;
    
    private final UUID uuid = UUID.randomUUID();

    @Before
    public void initPost() {
        when(request.getMethod()).thenReturn(Method.POST);
        
        when(options.getResumable()).thenReturn(tusResumable );
        when(options.getMaxSize()).thenReturn(maxSize);
        
        when(pool.newUploader()).thenReturn(Observable.just(uuid));
    }

    
    
    @Test
    public void testPostResponseSetTusResumableHeader() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            t.getHeaders().stream().filter(th -> th.getName().equals("Tus-Resumable")).findFirst()
                    .map(TusHeader::getValue)
                    .map(value -> assertThat(value).isEqualTo(tusResumable))
                    .orElseThrow(() -> Failures.instance().failure("Missing Tus-Resumable header in the response"));
        });
    }
    
    @Test
    public void testPostResponseSetLocationHeader() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> {
            t.getHeaders().stream().filter(th -> th.getName().equals("Location")).findFirst()
                    .map(TusHeader::getValue)
                    .map(value -> assertThat(value).endsWith(uuid.toString()))
                    .orElseThrow(() -> Failures.instance().failure("Missing Tus-Resumable header in the response"));
        });
    }
    
    @Test
    public void testPostResponseHasCreatedStatus() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> { 
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.CREATED);
        });
    }
    
    @Test
    public void testInvalidProtocolVersionResponds400() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of("nope"));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> { 
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.BAD_REQUEST);
        });
    }
    
    @Test
    public void testUploadLengthNotSetResponds400() {
     // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> { 
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.BAD_REQUEST);
        });
    }
    
    @Test
    public void testTooLargeUploadResponds419() {
     // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize + 1)));

        // act
        Observable<TusResponse> response = tusRx.handle(request);

        // assert
        response.single().toBlocking().subscribe(t -> { 
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
        });
    }
}
