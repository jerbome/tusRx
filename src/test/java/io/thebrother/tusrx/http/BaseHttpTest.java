package io.thebrother.tusrx.http;

import org.junit.ClassRule;

import io.thebrother.tusrx.server.TestServerRule;

public abstract class BaseHttpTest {

    @ClassRule
    public static TestServerRule serverRule = new TestServerRule(true);

}
