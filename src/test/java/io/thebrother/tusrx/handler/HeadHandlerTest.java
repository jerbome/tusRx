package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import io.thebrother.tusrx.handler.RequestHandlerTest.ResponseHeaderAssert;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.upload.TusUpload;

import rx.Observable;


public class HeadHandlerTest extends RequestHandlerTest {
    @Before
    public void init() {
        this.handler = new HeadHandler(options, request, pool);
    }
    
    @Test
    public void testHeadNoUploadReturns404() {
        when(pool.getUploader(any())).thenReturn(Optional.empty());
        
        Observable<TusResponse> response = handler.handle();
        
        response.single().toBlocking().subscribe(tr -> {
            assertThat(tr.getStatusCode() == 404);
        });
    }
    
    @Test
    public void testHeadUploadInProgressReturns200() {
        TusUpload mockUpload = mock(TusUpload.class);
        when(mockUpload.getUploadLength()).thenReturn(200L);
        when(mockUpload.getOffset()).thenReturn(new AtomicLong(10L));
        when(pool.getUploader(any())).thenReturn(Optional.of(mockUpload));
        
        Observable<TusResponse> response = handler.handle();
        
        response.single().toBlocking().subscribe(tr -> {
            assertThat(tr.getStatusCode() == 200);
        });
    }
}
