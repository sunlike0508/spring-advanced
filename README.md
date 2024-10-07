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


<img width="690" alt="Screenshot 2024-10-07 at 23 25 40" src="https://github.com/user-attachments/assets/7db6c79d-83c5-433e-ad3b-754be1e05aca">
<img width="694" alt="Screenshot 2024-10-07 at 23 25 51" src="https://github.com/user-attachments/assets/22a65f08-7688-4d7b-a186-070fb6a00203">

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

## ThreadLocal

쓰레드 로컬은 해당 쓰레드만 접근할 수 있는 특별한 저장소를 말한다. 

쉽게 이야기해서 물건 보관 창구를 떠올리면 된다. 

여러 사람이 같은 물건 보관 창구를 사용하더라도 창구 직원은 사용자를 인식해서 사용자별로 확실하게 물건을 구분 해준다.

사용자A, 사용자B 모두 창구 직원을 통해서 물건을 보관하고, 꺼내지만 창구 지원이 사용자에 따라 보관한 물건을 구분 해주는 것이다.

**일반적인 변수 필드**

여러 쓰레드가 같은 인스턴스의 필드에 접근하면 처음 쓰레드가 보관한 데이터가 사라질 수 있다.

바로 위의 사진이 예시

**쓰레드 로컬**

쓰레드 로컬을 사용하면 각 쓰레드마다 별도의 내부 저장소를 제공한다. 

따라서 같은 인스턴스의 쓰레드 로컬 필드에 접 근해도 문제 없다.

<img width="681" alt="Screenshot 2024-10-07 at 23 32 12" src="https://github.com/user-attachments/assets/9056843b-60aa-4a5d-b17e-699bb1ccada6">

`thread-A` 가 `userA` 라는 값을 저장하면 쓰레드 로컬은 `thread-A` 전용 보관소에 데이터를 안전하게 보관한다.

<img width="708" alt="Screenshot 2024-10-07 at 23 32 41" src="https://github.com/user-attachments/assets/fc8b9832-9404-4484-814d-eb295999105b">

`thread-B` 가 `userB` 라는 값을 저장하면 쓰레드 로컬은 `thread-B` 전용 보관소에 데이터를 안전하게 보관한다.

<img width="714" alt="Screenshot 2024-10-07 at 23 33 04" src="https://github.com/user-attachments/assets/29d869f9-a759-4301-93ca-841307e9dd12">

쓰레드 로컬을 통해서 데이터를 조회할 때도 `thread-A` 가 조회하면 쓰레드 로컬은 `thread-A` 전용 보관소에서 `userA` 데이터를 반환해준다. 

물론 `thread-B` 가 조회하면 `thread-B` 전용 보관소에서 `userB` 데이터를 반환해준다.

자바는 언어차원에서 쓰레드 로컬을 지원하기 위한 `java.lang.ThreadLocal` 클래스를 제공한다.

## 주의 사항

쓰레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 쓰레드 풀을 사용하는 경우에 심각한 문제가 발생할 수 있다.

**사용자A 저장 요청**

<img width="679" alt="Screenshot 2024-10-07 at 23 50 39" src="https://github.com/user-attachments/assets/d133105a-fb40-4ccf-a981-d50a95ea1432">

1. 사용자A가 저장 HTTP를 요청했다.
2. WAS는 쓰레드 풀에서 쓰레드를 하나 조회한다.
3. 쓰레드 `thread-A` 가 할당되었다.
4. `thread-A` 는 `사용자A` 의 데이터를 쓰레드 로컬에 저장한다.
5. 쓰레드 로컬의 `thread-A` 전용 보관소에 `사용자A` 데이터를 보관한다.

**사용자A 저장 요청 종료**

<img width="689" alt="Screenshot 2024-10-07 at 23 49 51" src="https://github.com/user-attachments/assets/352d2213-01e6-4abe-b006-bb9662d7cfe8">

1. 사용자A의 HTTP 응답이 끝난다.
2. WAS는 사용이 끝난 `thread-A` 를 쓰레드 풀에 반환한다. 쓰레드를 생성하는 비용은 비싸기 때문에 쓰레드를 제거하지 않고, 보통 쓰레드 풀을 통해서 쓰레드를 재사용한다.
3. `thread-A` 는 쓰레드풀에 아직 살아있다. 따라서 쓰레드 로컬의 `thread-A` 전용 보관소에 `사용자A` 데이터도 함께 살아있게 된다.


**사용자B 조회 요청**

<img width="676" alt="Screenshot 2024-10-07 at 23 51 00" src="https://github.com/user-attachments/assets/b50c9cdf-aeee-4369-b5b9-bdbaa1f17e6d">

1. 사용자B가 조회를 위한 새로운 HTTP 요청을 한다.
2. WAS는 쓰레드 풀에서 쓰레드를 하나 조회한다.
3. 쓰레드 `thread-A` 가 할당되었다. (물론 다른 쓰레드가 할당될 수 도 있다.)
4. 이번에는 조회하는 요청이다. `thread-A` 는 쓰레드 로컬에서 데이터를 조회한다. 
5. 쓰레드 로컬은 `thread-A` 전용 보관소에 있는 `사용자A` 값을 반환한다.
6. 결과적으로 `사용자A` 값이 반환된다.
7. 사용자B는 사용자A의 정보를 조회하게 된다.

결과적으로 사용자B는 사용자A의 데이터를 확인하게 되는 심각한 문제가 발생하게 된다.

이런 문제를 예방하려면 사용자A의 요청이 끝날 때 쓰레드 로컬의 값을 `ThreadLocal.remove()` 를 통해서 꼭 제거해야 한다.

쓰레드 로컬을 사용할 때는 이 부분을 꼭! 기억하자.
















