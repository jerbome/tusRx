package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.thebrother.tusrx.entry.TusRequest.Method;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.upload.TusUpload;

import rx.Observable;

public class PatchHandlerTest extends RequestHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(PatchHandlerTest.class);

    private final String tusResumable = "resumable";
    private final long maxSize = 100L;

    private final UUID uuid = UUID.randomUUID();

    @Mock
    private TusUpload upload;

    @Before
    public void initPatch() {
        when(request.getMethod()).thenReturn(Method.PATCH);

        when(options.getResumable()).thenReturn(tusResumable);
        when(options.getMaxSize()).thenReturn(maxSize);

        when(pool.getUploader(uuid)).thenReturn(Optional.of(upload));
        
        handler = new PatchHandler(options, request, pool);
    }

    @Test
    public void testErrorDoesNotBreak() {
        // arrange
        when(request.getUuid()).thenReturn(uuid);
        when(request.getHeader("Upload-Offset")).thenReturn(Optional.of("0"));
        when(upload.getOffset()).thenReturn(new AtomicLong(0L));
        when(upload.uploadChunk(request))
                .thenReturn(Observable.just(1L, 2L, 3L).concatWith(Observable.error(new IOException())));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.toBlocking().subscribe(tr -> {
            assertThat(tr.getStatusCode()).isEqualTo(204);
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(tr);
            responseAssert.hasHeader("Upload-Offset", "6");
            responseAssert.hasHeader("Tus-Resumable", tusResumable);
        }, x -> logger.info("", x));
    }
    
    @Test
    public void testErrorDoesNotBreak2() {
        // arrange
        when(request.getUuid()).thenReturn(uuid);
        when(request.getHeader("Upload-Offset")).thenReturn(Optional.of("10"));
        when(upload.getOffset()).thenReturn(new AtomicLong(10L));
        when(upload.uploadChunk(request))
                .thenReturn(Observable.just(1L, 2L, 3L).concatWith(Observable.error(new IOException())));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.toBlocking().subscribe(tr -> {
            assertThat(tr.getStatusCode()).isEqualTo(204);
            ResponseHeaderAssert responseAssert = new ResponseHeaderAssert(tr);
            responseAssert.hasHeader("Upload-Offset", "16");
            responseAssert.hasHeader("Tus-Resumable", tusResumable);
        }, x -> logger.info("", x));
    }
    
    @Test
    public void testWrongOffsetResponds409() {
     // arrange
        when(request.getUuid()).thenReturn(uuid);
        when(request.getHeader("Upload-Offset")).thenReturn(Optional.of("10"));
        when(upload.getOffset()).thenReturn(new AtomicLong(50L));
        when(upload.uploadChunk(request))
                .thenReturn(Observable.just(1L, 2L, 3L).concatWith(Observable.error(new IOException())));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.toBlocking().subscribe(tr -> {
            assertThat(tr.getStatusCode()).isEqualTo(409);
        }, x -> logger.info("", x));
    }
    
    @Test
    public void testSuccessfulChunckUploadIncrementsOffset() {
     // arrange
        when(request.getUuid()).thenReturn(uuid);
        when(request.getHeader("Upload-Offset")).thenReturn(Optional.of("0"));
        AtomicLong offset = new AtomicLong(0L);
        when(upload.getOffset()).thenReturn(offset);
        when(upload.uploadChunk(request))
                .thenReturn(Observable.just(1L, 2L, 3L).concatWith(Observable.error(new IOException())));

        // act
        Observable<TusResponse> response = handler.handle();
        response.toBlocking().subscribe();

        // assert
        assertThat(offset.get()).isEqualTo(6L);
    }
    
    @Test
    public void testNoUploadFoundResponds404() {
     // arrange
        when(request.getUuid()).thenReturn(UUID.randomUUID());
        when(request.getHeader("Upload-Offset")).thenReturn(Optional.of("0"));

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        response.toBlocking().subscribe(tr -> { 
            assertThat(tr.getStatusCode()).isEqualTo(404);
        });
    }
}
