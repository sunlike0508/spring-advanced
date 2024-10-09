package hello.advanced.trace.strategy;


import hello.advanced.trace.strategy.code.template.TimeLogTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TemplateCallbackTest {

    @Test
    void callback() {

        TimeLogTemplate template = new TimeLogTemplate();

        template.execute(() -> log.info("비즈니스 로직 1실행"));
        template.execute(() -> log.info("비즈니스 로직 2실행"));
    }
}

