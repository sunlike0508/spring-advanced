package hello.advanced.trace.strategy.code;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Context1 {

    private final Strategy strategy;


    public Context1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {

        long startTime = System.currentTimeMillis();

        strategy.call();

        long endTime = System.currentTimeMillis();

        log.info("resultTime = {} ", endTime - startTime);
    }
}
