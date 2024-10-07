# 스프링 핵심

# ThreadLocal

## 동시성 문제

**동시성 문제**

`FieldLogTrace` 는 싱글톤으로 등록된 스프링 빈이다. 이 객체의 인스턴스가 애플리케이션에 딱 1 존재한다는 뜻이다. 

이렇게 하나만 있는 인스턴스의 `FieldLogTrace.traceIdHolder` 필드를 여러 쓰레드가 동시에 접근하기 때문에 문제가 발생한다.

실무에서 한번 나타나면 개발자를 가장 괴롭히는 문제도 바로 이러한 동시성 문제이다.

```java
public class FieldServiceTest {

    private FieldService fieldService = new FieldService();

    @Test
    void field() throws InterruptedException {
        log.info("main test");

        Runnable userA = () -> fieldService.logic("userA");
        Runnable userB = () -> fieldService.logic("userB");

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
        sleep(100);
        threadB.start();

        sleep(3000);

        log.info("main exit");
    }
}
```

0.1초 이후에 `thread-B` 가 `userB` 의 값을 `nameStore` 에 보관한다. 

기존에 `nameStore` 에 보관되어 있던 `userA` 값은 제거되고 `userB` 값이 저장된다.

`thread-A` 의 호출이 끝나면서 `nameStore` 의 결과를 반환 받는데, 이때 `nameStore` 는 앞의 2번에서 `userB` 의 값으로 대체되었다. 

따라서 기대했던 `userA` 의 값이 아니라 `userB` 의 값이 반환된다. 

`thread-B` 의 호출이 끝나면서 `nameStore` 의 결과인 `userB` 를 반환 받는다.


**동시성 문제**
결과적으로 `Thread-A` 입장에서는 저장한 데이터와 조회한 데이터가 다른 문제가 발생한다. 

이처럼 여러 쓰레드가 동시에 같은 인스턴스의 필드 값을 변경하면서 발생하는 문제를 동시성 문제라 한다. 

이런 동시성 문제는 여러 쓰레드가 같은 인스턴스의 필드에 접근해야 하기 때문에 트래픽이 적은 상황에서는 확률상 잘 나타나지 않고, 트래픽이 점점 많아 질 수 록 자주 발생한다.

특히 스프링 빈 처럼 싱글톤 객체의 필드를 변경하며 사용할 때 이러한 동시성 문제를 조심해야 한다.

**참고**
이런 동시성 문제는 지역 변수에서는 발생하지 않는다. 

지역 변수는 쓰레드마다 각각 다른 메모리 영역이 할당된다.

동시성 문제가 발생하는 곳은 같은 인스턴스의 필드(주로 싱글톤에서 자주 발생), 또는 static 같은 공용 필드에 접근할 때 발생한다.

동시성 문제는 값을 읽기만 하면 발생하지 않는다.

어디선가 값을 변경하기 때문에 발생한다.

그렇다면 지금처럼 싱글톤 객체의 필드를 사용하면서 동시성 문제를 해결하려면 어떻게 해야할까? 

다시 파라미터를 전달하는 방식으로 돌아가야 할까? 

이럴 때 사용하는 것이 바로 쓰레드 로컬이다.














