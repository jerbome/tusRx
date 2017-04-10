package io.thebrother.tusrx.client;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;

public class TestClient {
	private static final Logger logger = LoggerFactory.getLogger(TestClient.class);
	
	public static void main(String[] args) {

        HttpClient.newClient("localhost", 8080)
                  .enableWireLogging("hello-client", LogLevel.ERROR)
                  .createOptions("/files")
                  .doOnNext(resp -> logger.info(resp.toString()))
                  .flatMap(resp -> resp.getContent()
                                       .map(bb -> bb.toString(Charset.defaultCharset())))
                  .toBlocking()
                  .forEach(logger::info);
	}
}
