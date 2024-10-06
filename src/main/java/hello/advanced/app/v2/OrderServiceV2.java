package hello.advanced.app.v2;


import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepository;
    private final HelloTraceV2 traceV2;


    public void orderItem(TraceId traceId, String itemId) {
        TraceStatus status = traceV2.beginSync(traceId, "OrderServiceV1.orderItem");

        try {
            orderRepository.save(status.getTraceId(), itemId);

            traceV2.end(status);

        } catch(Exception e) {
            traceV2.exception(status, e);
            throw e;
        }
    }
}
