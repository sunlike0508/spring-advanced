package hello.advanced.trace.strategy.code.strategy;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Context2 {

    public void execute(Strategy strategy) {

        long startTime = System.currentTimeMillis();

        strategy.call();

        long endTime = System.currentTimeMillis();

        log.info("resultTime = {} ", endTime - startTime);
    }
}
