package hello.advanced.app.v2;


import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {

    private final HelloTraceV2 traceV2;

    public void save(TraceId traceId, String itemId) {

        TraceStatus status = traceV2.beginSync(traceId, "OrderRepository.save");

        try {
            if(itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생");
            }

            sleep(1000);

            traceV2.end(status);

        } catch(Exception e) {
            traceV2.exception(status, e);
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
