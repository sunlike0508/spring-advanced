package hello.advanced.app.v1;


import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV1 {

    private final HelloTraceV1 traceV1;

    public void save(String itemId) {

        TraceStatus status = traceV1.begin("OrderRepositoryV1.save");

        try {
            if(itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생");
            }

            sleep(1000);

            traceV1.end(status);

        } catch(Exception e) {
            traceV1.exception(status, e);
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
