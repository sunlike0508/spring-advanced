package hello.advanced.trace.hellotrace.HelloTraceV1;

import hello.advanced.trace.TraceStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class HelloTraceV1Test {

    @Autowired
    private HelloTraceV1 helloTraceV1;

    @Test
    void begin() {
        TraceStatus status = helloTraceV1.begin("hello");

        helloTraceV1.end(status);
    }


    @Test
    void end() {
        TraceStatus status = helloTraceV1.begin("hello");

        helloTraceV1.exception(status, new IllegalStateException("exception"));
    }
}