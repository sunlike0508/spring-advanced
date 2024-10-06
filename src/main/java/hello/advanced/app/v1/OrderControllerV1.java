package hello.advanced.app.v1;


import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;
    private final HelloTraceV1 traceV1;

    @GetMapping("/v1/request")
    public String request(String itemId) {

        TraceStatus status = traceV1.begin("OrderControllerV1.request");

        try {
            orderService.orderItem(itemId);

            traceV1.end(status);

            return "ok";
        } catch(Exception e) {
            traceV1.exception(status, e);
            throw e;
        }
    }
}
