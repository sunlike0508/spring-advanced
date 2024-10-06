package hello.advanced.trace;

public class TraceStatus {

    private TraceId traceId;
    private Long startTimeMs;
    private String message;


    public TraceStatus(TraceId traceId, Long statTimeMs, String message) {
        this.message = message;
        this.startTimeMs = statTimeMs;
        this.traceId = traceId;
    }


    public String getMessage() {
        return message;
    }


    public Long getStartTimeMs() {
        return startTimeMs;
    }


    public TraceId getTraceId() {
        return traceId;
    }
}
