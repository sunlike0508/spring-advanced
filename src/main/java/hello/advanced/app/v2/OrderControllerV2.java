package hello.advanced.app.v2;


import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;
    private final HelloTraceV2 traceV2;

    @GetMapping("/v2/request")
    public String request(String itemId) {

        TraceStatus status = traceV2.begin("OrderController.request");

        try {
            orderService.orderItem(status.getTraceId(), itemId);

            traceV2.end(status);

            return "ok";
        } catch(Exception e) {
            traceV2.exception(status, e);
            throw e;
        }
    }
}
