package hello.advanced.trace.strategy;


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
