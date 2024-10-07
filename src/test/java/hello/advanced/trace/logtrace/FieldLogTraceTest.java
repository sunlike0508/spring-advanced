package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
class FieldLogTraceTest {


    FieldLogTrace fieldLogTrace = new FieldLogTrace();

    @Test
    void test1() {
        TraceStatus test1 = fieldLogTrace.begin("test1");
        TraceStatus test2 = fieldLogTrace.begin("test2");

        fieldLogTrace.end(test2);
        fieldLogTrace.end(test1);

    }
}
