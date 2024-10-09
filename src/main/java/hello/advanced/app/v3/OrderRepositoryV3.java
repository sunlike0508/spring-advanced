package hello.advanced.app.v3;


import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV2;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV3 {

    private final LogTrace trace;

    public void save(String itemId) {

        TraceStatus status = trace.begin("OrderRepository.save");

        try {
            if(itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생");
            }

            sleep(1000);

            trace.end(status);

        } catch(Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }


    private void sleep(int mSecond) {

        try {
            Thread.sleep(mSecond);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
