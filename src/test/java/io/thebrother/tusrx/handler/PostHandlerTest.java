package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.thebrother.tusrx.entry.TusRequest.Method;
import io.thebrother.tusrx.handler.PostHandler;
import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class PostHandlerTest extends RequestHandlerTest {

    private final String tusResumable = "resumableHeader";
    private final long maxSize = 100L;
    
    private final UUID uuid = UUID.randomUUID();

    @Before
    public void initPost() {
        when(request.getMethod()).thenReturn(Method.POST);
        
        when(options.getResumable()).thenReturn(tusResumable );
        when(options.getMaxSize()).thenReturn(maxSize);
        
        when(pool.newUploader(ArgumentMatchers.anyLong())).thenReturn(Observable.just(uuid));
        
        handler = new PostHandler(options, request, pool);
    }

    
    
    @Test
    public void testPostResponseSetTusResumableHeader() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(t);
            assertThat(responseAssert).hasHeader("Tus-Resumable", tusResumable);
        });
    }
    
    @Test
    public void testPostResponseSetLocationHeader() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> {
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(t);
            assertThat(responseAssert).hasHeaderEndingWith("Location", uuid.toString());
        });
    }
    
    @Test
    public void testPostResponseHasCreatedStatus() {
        // arrange
        when(request.getHeader("Tus-Resumable")).thenReturn(Optional.of(tusResumable));
        when(request.getHeader("Upload-Length")).thenReturn(Optional.of(Long.toString(maxSize)));

        // act
        Observable<TusResponse> response = handler.handle();

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
        Observable<TusResponse> response = handler.handle();

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
        Observable<TusResponse> response = handler.handle();

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
        Observable<TusResponse> response = handler.handle();

        // assert
        response.single().toBlocking().subscribe(t -> { 
            assertThat(t.getStatus()).isEqualTo(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
        });
    }
}
