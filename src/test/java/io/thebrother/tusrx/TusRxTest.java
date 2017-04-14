package io.thebrother.tusrx;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.upload.UploaderPool;

public abstract class TusRxTest {
    
    protected TusRx tusRx;

    @Mock protected Options options;
    @Mock protected UploaderPool pool;
    @Mock protected TusRequest request;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        
        tusRx = new TusRx(options, pool);
    }
    
    
}
