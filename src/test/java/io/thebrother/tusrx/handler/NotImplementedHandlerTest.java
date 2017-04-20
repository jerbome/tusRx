package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import io.thebrother.tusrx.response.TusResponse;

import rx.Observable;

public class NotImplementedHandlerTest extends RequestHandlerTest {
    @Before
    public void init() {
        this.handler = new NotImplementedHandler();
    }

    @Test
    public void testResponds501() {
        // arrange

        // act
        Observable<TusResponse> response = handler.handle();

        // assert
        assertThat(response.toBlocking().single().getStatusCode()).isEqualTo(501);
    }
}
