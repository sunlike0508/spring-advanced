package hello.advanced.trace.strategy;


import hello.advanced.trace.strategy.code.Context1;
import hello.advanced.trace.strategy.code.Strategy;
import hello.advanced.trace.strategy.code.StrategyLogic1;
import hello.advanced.trace.strategy.code.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ContextV1Test {

    @Test
    void templateMethod() {
        logic1();
        logic2();
    }

    @Test
    void strategyMethod() {
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        StrategyLogic2 strategyLogic2 = new StrategyLogic2();

        Context1 context1 = new Context1(strategyLogic1);

        context1.execute();

        Context1 context2 = new Context1(strategyLogic2);

        context2.execute();
    }


    @Test
    void strategyMethodV2() {
        //Strategy strategy = () -> log.info("비즈니스 로직 1 실행");

        Context1 context1 = new Context1(() -> log.info("비즈니스 로직 1 실행"));

        context1.execute();

        Context1 context2 = new Context1(() -> log.info("비즈니스 로직 2 실행"));

        context2.execute();
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
