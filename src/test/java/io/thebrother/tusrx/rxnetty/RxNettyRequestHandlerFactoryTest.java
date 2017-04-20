package io.thebrother.tusrx.rxnetty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.handler.*;
import io.thebrother.tusrx.upload.UploaderPool;

public class RxNettyRequestHandlerFactoryTest {
    @Mock
    private Options options;
    @Mock
    private UploaderPool pool;
    @Mock
    private HttpServerRequest<ByteBuf> request;

    private RxNettyRequestHandlerFactory factory;
    
    @Before
    public void setupTest() {
        MockitoAnnotations.initMocks(this);
        factory = new RxNettyRequestHandlerFactory(options, pool);
    }
    
    @Test
    public void testOptionsRequestMakesOptionsHandler() {
        // arrange
        when(request.getHttpMethod()).thenReturn(HttpMethod.OPTIONS);
        
        //act
        RequestHandler handler = factory.makeHandler(request);
        
        // assert
        assertThat(handler).isInstanceOf(OptionHandler.class);
    }
    
    @Test
    public void testPostRequestMakesPostHandler() {
        // arrange
        when(request.getHttpMethod()).thenReturn(HttpMethod.POST);
        
        //act
        RequestHandler handler = factory.makeHandler(request);
        
        // assert
        assertThat(handler).isInstanceOf(PostHandler.class);
    }
    
    @Test
    public void testPatchRequestMakesPatchHandler() {
        // arrange
        when(request.getHttpMethod()).thenReturn(HttpMethod.PATCH);
        
        //act
        RequestHandler handler = factory.makeHandler(request);
        
        // assert
        assertThat(handler).isInstanceOf(PatchHandler.class);
    }
    
    @Test
    public void testHeadRequestMakesHeadHandler() {
        // arrange
        when(request.getHttpMethod()).thenReturn(HttpMethod.HEAD);
        
        //act
        RequestHandler handler = factory.makeHandler(request);
        
        // assert
        assertThat(handler).isInstanceOf(HeadHandler.class);
    }
    
    @Test
    public void testBogusRequestMakesNotImplementedHandler() {
        // arrange
        when(request.getHttpMethod()).thenReturn(new HttpMethod("FOO"));
        
        //act
        RequestHandler handler = factory.makeHandler(request);
        
        // assert
        assertThat(handler).isInstanceOf(NotImplementedHandler.class);
    }
}
