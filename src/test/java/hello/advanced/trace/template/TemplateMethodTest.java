package hello.advanced.trace.template;

import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogic1;
import hello.advanced.trace.template.code.SubClassLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TemplateMethodTest {

    @Test
    void templateMethod() {
        logic1();
        logic2();
    }


    @Test
    void templateMethodV1() {
        AbstractTemplate abstractTemplate = new SubClassLogic1();
        abstractTemplate.execute();

        AbstractTemplate abstractTemplate2 = new SubClassLogic2();
        abstractTemplate2.execute();
    }

    @Test
    void templateMethodV2() {
        AbstractTemplate abstractTemplate = new AbstractTemplate() {

            @Override
            protected void call() {
                log.info("비즈니스 로직111 실행");
            }
        };

        System.out.println(abstractTemplate.getClass());

        abstractTemplate.execute();

        AbstractTemplate abstractTemplate2 = new AbstractTemplate() {

            @Override
            protected void call() {
                log.info("비즈니스 로직222 실행");
            }
        };

        abstractTemplate2.execute();
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
