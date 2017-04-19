package io.thebrother.tusrx.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AssertDelegateTarget;
import org.assertj.core.internal.Failures;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.thebrother.tusrx.Options;
import io.thebrother.tusrx.entry.TusRequest;
import io.thebrother.tusrx.handler.RequestHandler;
import io.thebrother.tusrx.http.TusHeader;
import io.thebrother.tusrx.response.TusResponse;
import io.thebrother.tusrx.upload.UploaderPool;

public abstract class RequestHandlerTest {

    protected RequestHandler handler;

    @Mock
    protected Options options;
    @Mock
    protected UploaderPool pool;
    @Mock
    protected TusRequest request;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public static class ResponseHeaderAssert implements AssertDelegateTarget {
        private final TusResponse response;

        public ResponseHeaderAssert(TusResponse response) {
            this.response = response;
        }

        public void hasHeader(String headerName, String expectedHeaderValue) {
            response.getHeaders().stream().filter(th -> th.getName().equals(headerName)).findFirst()
                    .map(TusHeader::getValue)
                    .map(value -> assertThat(value).isEqualTo(expectedHeaderValue))
                    .orElseThrow(() -> Failures.instance().failure(headerName + " header not present in response"));
        }

        public void hasHeaderEndingWith(String headerName, String expectedHeaderValue) {
            response.getHeaders().stream().filter(th -> th.getName().equals(headerName)).findFirst()
                    .map(TusHeader::getValue)
                    .map(value -> assertThat(value).endsWith(expectedHeaderValue))
                    .orElseThrow(() -> Failures.instance().failure(headerName + " header not present in response"));
        }

        public void doesNotHaveHeader(String headerName) {
            assertThat(response.getHeaders().stream().noneMatch(th -> th.getName().equals(headerName)))
                    .as("Checking absence of header[%s] on the response", headerName).isTrue();

        }
    }

}
