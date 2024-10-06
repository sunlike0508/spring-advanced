package hello.advanced.trace.hellotrace.HelloTraceV1;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class HelloTraceV2Test {

    @Autowired
    private HelloTraceV2 traceV2;

    @Test
    void begin() {
        TraceStatus status1 = traceV2.begin("hello1");
        TraceStatus status2 = traceV2.beginSync(status1.getTraceId(), "hello2");
        traceV2.end(status2);
        traceV2.end(status1);
    }


    @Test
    void end() {
        TraceStatus status1 = traceV2.begin("hello1");
        TraceStatus status2 = traceV2.beginSync(status1.getTraceId(), "hello2");

        traceV2.exception(status2, new IllegalStateException());
        traceV2.exception(status1, new IllegalStateException());
    }
}