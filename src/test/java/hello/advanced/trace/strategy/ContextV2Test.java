package hello.advanced.trace.strategy;


import hello.advanced.trace.strategy.code.Context1;
import hello.advanced.trace.strategy.code.Context2;
import hello.advanced.trace.strategy.code.StrategyLogic1;
import hello.advanced.trace.strategy.code.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ContextV2Test {

    @Test
    void templateMethod() {
        logic1();
        logic2();
    }

    @Test
    void strategyMethod() {
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        StrategyLogic2 strategyLogic2 = new StrategyLogic2();

        Context2 context = new Context2();
        context.execute(strategyLogic1);
        context.execute(strategyLogic2);

    }


    private void logic1() {
        long startTime = System.currentTimeMillis();

        log.info("비즈니스 로직1 실행");

        long endTime = System.currentTimeMillis();

        log.info("resultTime = {} ", endTime - startTime);
    }


    private void logic2() {
        long startTime = System.currentTimeMillis();

        log.info("비즈니스 로직2 실행");

        long endTime = System.currentTimeMillis();

        log.info("resultTime = {} ", endTime - startTime);
    }
}
