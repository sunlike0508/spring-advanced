package hello.advanced.trace.template.code;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTemplate {

    public void execute() {
        long startTime = System.currentTimeMillis();

        call();

        long endTime = System.currentTimeMillis();

        log.info("resultTime = {} ", endTime - startTime);
    }

    protected abstract void call();
}
