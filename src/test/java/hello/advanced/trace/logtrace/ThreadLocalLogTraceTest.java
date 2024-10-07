package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ThreadLocalLogTraceTest {


    ThreadLocalLogTrace threadLocalLogTrace = new ThreadLocalLogTrace();

    @Test
    void test1() {
        TraceStatus test1 = threadLocalLogTrace.begin("test1");
        TraceStatus test2 = threadLocalLogTrace.begin("test2");

        threadLocalLogTrace.end(test2);
        threadLocalLogTrace.end(test1);

    }
}
