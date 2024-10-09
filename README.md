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

# 템플릿 메서드 패턴

**변하는 것과 변하지 않는 것을 분리**

좋은 설계는 변하는 것과 변하지 않는 것을 분리하는 것이다.

여기서 핵심 기능 부분은 변하고, 로그 추적기를 사용하는 부분은 변하지 않는 부분이다. 

이 둘을 분리해서 모듈화해야 한다.

템플릿 메서드 패턴(Template Method Pattern)은 이런 문제를 해결하는 디자인 패턴이다

<img width="704" alt="Screenshot 2024-10-09 at 15 45 36" src="https://github.com/user-attachments/assets/3ae5ad47-ee37-47f5-b8aa-d0676024ebf3">

템플릿 메서드 패턴은 이름 그대로 템플릿을 사용하는 방식이다. 

템플릿은 기준이 되는 거대한 틀이다. 템플릿이라는 틀에 변하지 않는 부분을 몰아둔다. 

그리고 일부 변하는 부분을 별도로 호출해서 해결한다.

`AbstractTemplate` 코드를 보자. 변하지 않는 부분인 시간 측정 로직을 몰아둔 것을 확인할 수 있다. 

이제 이것이 하나의 템플릿이 된다. 

그리고 템플릿 안에서 변하는 부분은 `call()` 메서드를 호출해서 처리한다. 

템플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿 코드를 둔다. 

그리고 변하는 부분은 자식 클래스에 두고 상속과 오버라이딩을 사용해서 처리한다.

<img width="708" alt="Screenshot 2024-10-09 at 15 50 09" src="https://github.com/user-attachments/assets/ae2a13a9-583e-494f-b2c4-18980405525f">

`template1.execute()` 를 호출하면 템플릿 로직인 `AbstractTemplate.execute()` 를 실행한다. 

여기서 중 간에 `call()` 메서드를 호출하는데, 이 부분이 오버라이딩 되어있다.

따라서 현재 인스턴스인 `SubClassLogic1` 인스턴스의 `SubClassLogic1.call()` 메서드가 호출된다.

템플릿 메서드 패턴은 이렇게 다형성을 사용해서 변하는 부분과 변하지 않는 부분을 분리하는 방법이다.

**좋은 설계란?**

좋은 설계라는 것은 무엇일까? 수 많은 멋진 정의가 있겠지만, 진정한 좋은 설계는 바로 **변경**이 일어날 때 자연스럽게 드러난다.

지금까지 로그를 남기는 부분을 모아서 하나로 모듈화하고, 비즈니스 로직 부분을 분리했다. 

여기서 만약 로그를 남기는 로직을 변경해야 한다고 생각해보자. 

그래서 `AbstractTemplate` 코드를 변경해야 한다 가정해보자. 

단순히 `AbstractTemplate` 코드만 변경하면 된다.

템플릿이 없는 `V3` 상태에서 로그를 남기는 로직을 변경해야 한다고 생각해보자.

이 경우 모든 클래스를 다 찾아서 고쳐야 한다. 클래스가 수백 개라면 생각만해도 끔찍하다.

**단일 책임 원칙(SRP)**

`V4` 는 단순히 템플릿 메서드 패턴을 적용해서 소스코드 몇줄을 줄인 것이 전부가 아니다.

로그를 남기는 부분에 단일 책임 원칙(SRP)을 지킨 것이다. 

변경 지점을 하나로 모아서 변경에 쉽게 대처할 수 있는 구조를 만든 것이다.

## 템플릿 메서드 패턴 - 정의

<img width="222" alt="Screenshot 2024-10-09 at 16 21 33" src="https://github.com/user-attachments/assets/9b192a5f-708e-4d42-a6ee-450fa270de85">

GOF 디자인 패턴에서는 템플릿 메서드 패턴을 다음과 같이 정의했다.

템플릿 메서드 디자인 패턴의 목적은 다음과 같습니다.

"작업에서 알고리즘의 골격을 정의하고 일부 단계를 하위 클래스로 연기합니다. 템플릿 메서드를 사용하면 하위 클래스가 알고리즘의 구조를 변경하지 않고도 알고리즘의 특정 단계를 재정의할 수 있습니다." [GOF]

GOF 템플릿 메서드 패턴 정의

부모 클래스에 알고리즘의 골격인 템플릿을 정의하고, 일부 변경되는 로직은 자식 클래스에 정의하는 것이다. 

이렇게 하면 자식 클래스가 알고리즘의 전체 구조를 변경하지 않고, 특정 부분만 재정의할 수 있다. 

결국 상속과 오버라이딩을 통한 다형성으로 문제를 해결하는 것이다.

**하지만** 템플릿 메서드 패턴은 상속을 사용한다. 

따라서 상속에서 오는 단점들을 그대로 안고간다. 

특히 자식 클래스가 부모 클 래스와 컴파일 시점에 강하게 결합되는 문제가 있다. 

이것은 의존관계에 대한 문제이다. 

자식 클래스 입장에서는 부모 클래스의 기능을 전혀 사용하지 않는다.

이번 장에서 지금까지 작성했던 코드를 떠올려보자. 

자식 클래스를 작성할 때 부모 클래스의 기능을 사용한 것이 있었던가?

그럼에도 불구하고 템플릿 메서드 패턴을 위해 자식 클래스는 부모 클래스를 상속 받고 있다.

상속을 받는 다는 것은 특정 부모 클래스를 의존하고 있다는 것이다. 

자식 클래스의 `extends` 다음에 바로 부모 클래 스가 코드상에 지정되어 있다. 

따라서 부모 클래스의 기능을 사용하든 사용하지 않든 간에 부모 클래스를 강하게 의존하게 된다. 

여기서 강하게 의존한다는 뜻은 자식 클래스의 코드에 부모 클래스의 코드가 명확하게 적혀 있다는 뜻이다. 

UML에서 상속을 받으면 삼각형 화살표가 `자식 -> 부모` 를 향하고 있는 것은 이런 의존관계를 반영하는 것이다.

자식 클래스 입장에서는 부모 클래스의 기능을 전혀 사용하지 않는데, 부모 클래스를 알아야한다. 

이것은 좋은 설계가 아니다. 

그리고 이런 잘못된 의존관계 때문에 부모 클래스를 수정하면, 자식 클래스에도 영향을 줄 수 있다. 

추가로 템플릿 메서드 패턴은 상속 구조를 사용하기 때문에, 별도의 클래스나 익명 내부 클래스를 만들어야 하는 부분도 복잡하다.

지금까지 설명한 이런 부분들을 더 깔끔하게 개선하려면 어떻게 해야할까?

템플릿 메서드 패턴과 비슷한 역할을 하면서 상속의 단점을 제거할 수 있는 디자인 패턴이 바로 전략 패턴(Strategy Pattern)이다.

# 전략 패턴

탬플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿을 두고, 변하는 부분을 자식 클래스에 두어서 상속을 사용해서 문제를 해결했다.

전략 패턴은 변하지 않는 부분을 `Context` 라는 곳에 두고, 변하는 부분을 `Strategy` 라는 인터페이스를 만들고 해당 인터페이스를 구현하도록 해서 문제를 해결한다. 

상속이 아니라 위임으로 문제를 해결하는 것이다.

전략 패턴에서 `Context` 는 변하지 않는 템플릿 역할을 하고, `Strategy` 는 변하는 알고리즘 역할을 한다.

GOF 디자인 패턴에서 정의한 전략 패턴의 의도는 다음과 같다.

알고리즘 제품군을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만들자. 

전략을 사용하면 알고리즘을 사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다.

<img width="710" alt="Screenshot 2024-10-09 at 16 41 45" src="https://github.com/user-attachments/assets/fc65a01c-7162-4e83-875d-b66ac46bb8ec">

`ContextV1` 은 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드이다. 

전략 패턴에서는 이것을 컨텍스트(문 맥)이라 한다.

쉽게 이야기해서 컨텍스트(문맥)는 크게 변하지 않지만, 그 문맥 속에서 `strategy` 를 통해 일부 전략이 변경된다 생각하면 된다.

`Context` 는 내부에 `Strategy strategy` 필드를 가지고 있다. 이 필드에 변하는 부분인 `Strategy` 의 구현체를 주입하면 된다.

전략 패턴의 핵심은 `Context` 는 `Strategy` 인터페이스에만 의존한다는 점이다. 

덕분에 `Strategy` 의 구현체를 변 경하거나 새로 만들어도 `Context` 코드에는 영향을 주지 않는다.

어디서 많이 본 코드 같지 않은가? 그렇다. 바로 스프링에서 의존관계 주입에서 사용하는 방식이 바로 전략 패턴이다.

<img width="691" alt="Screenshot 2024-10-09 at 16 54 32" src="https://github.com/user-attachments/assets/44735c15-38ec-4c8b-a37b-5af050d23044">

1. `Context` 에 원하는 `Strategy` 구현체를 주입한다.
2. 클라이언트는 `context` 를 실행한다.
3. `context` 는 `context` 로직을 시작한다.
4. `context` 로직 중간에 `strategy.call()` 을 호출해서 주입 받은 `strategy` 로직을 실행한다. 
5. `context` 는 나머지 로직을 실행한다.


**정리**

지금까지 일반적으로 이야기하는 전략 패턴에 대해서 알아보았다. 

변하지 않는 부분을 `Context` 에 두고 변하는 부분 을 `Strategy` 를 구현해서 만든다. 

그리고 `Context` 의 내부 필드에 `Strategy` 를 주입해서 사용했다.

**선 조립, 후 실행**

여기서 이야기하고 싶은 부분은 `Context` 의 내부 필드에 `Strategy` 를 두고 사용하는 부분이다.

이 방식은 `Context` 와 `Strategy` 를 실행 전에 원하는 모양으로 조립해두고, 그 다음에 `Context` 를 실행하는 선조립, 후 실행 방식에서 매우 유용하다.

`Context` 와 `Strategy` 를 한번 조립하고 나면 이후로는 `Context` 를 실행하기만 하면 된다. 

우리가 스프링으로 애플리케이션을 개발할 때 애플리케이션 로딩 시점에 의존관계 주입을 통해 필요한 의존관계를 모두 맺어두고 난 다음에 실제 요청을 처리하는 것 과 같은 원리이다.

이 방식의 단점은 `Context` 와 `Strategy` 를 조립한 이후에는 전략을 변경하기가 번거롭다는 점이다. 

물론 `Context` 에 `setter` 를 제공해서 `Strategy` 를 넘겨 받아 변경하면 되지만, `Context` 를 싱글톤으로 사용할 때는 동시성 이슈 등 고려할 점이 많다. 

그래서 전략을 실시간으로 변경해야 하면 차라리 이전에 개발한 테스트 코드 처럼 `Context` 를 하나더 생성하고 그곳에 다른 `Strategy` 를 주입하는 것이 더 나은 선택일 수 있다.

이렇게 먼저 조립하고 사용하는 방식보다 더 유연하게 전략 패턴을 사용하는 방법은 없을까?

**전략 패턴 - 예제3**

이번에는 전략 패턴을 조금 다르게 사용해보자. 이전에는 `Context` 의 필드에 `Strategy` 를 주입해서 사용했다. 

이번에는 전략을 실행할 때 직접 파라미터로 전달해서 사용해보자.

<img width="697" alt="Screenshot 2024-10-09 at 17 04 52" src="https://github.com/user-attachments/assets/53dc9090-c68d-4e24-8956-0902759b5bc1">

1. 클라이언트는 `Context` 를 실행하면서 인수로 `Strategy` 를 전달한다. 
2. `Context` 는 `execute()` 로직을 실행한다.
3. `Context` 는 파라미터로 넘어온 `strategy.call()` 로직을 실행한다. 
4. `Context` 의 `execute()` 로직이 종료된다.

**정리**
`ContextV1` 은 필드에 `Strategy` 를 저장하는 방식으로 전략 패턴을 구사했다.

선 조립, 후 실행 방법에 적합하다.

`Context` 를 실행하는 시점에는 이미 조립이 끝났기 때문에 전략을 신경쓰지 않고 단순히 실행만 하면 된다.

`ContextV2` 는 파라미터에 `Strategy` 를 전달받는 방식으로 전략 패턴을 구사했다.

실행할 때 마다 전략을 유연하게 변경할 수 있다.

단점 역시 실행할 때마다 전략을 계속 지정해주어야 한다는 점이다.

**템플릿**

지금 우리가 해결하고 싶은 문제는 변하는 부분과 변하지 않는 부분을 분리하는 것이다.

변하지 않는 부분을 템플릿이라고 하고, 그 템플릿 안에서 변하는 부분에 약간 다른 코드 조각을 넘겨서 실행하는 것이 목적이다.

`ContextV1` , `ContextV2` 두 가지 방식 다 문제를 해결할 수 있지만, 어떤 방식이 조금 더 나아 보이는가?

지금 우리가 원하는 것은 애플리케이션 의존 관계를 설정하는 것처럼 선 조립, 후 실행이 아니다.

단순히 코드를 실행할 때 변하지 않는 템플릿이 있고, 그 템플릿 안에서 원하는 부분만 살짝 다른 코드를 실행하고 싶을 뿐이다.

따라서 우리가 고민하는 문제는 실행 시점에 유연하게 실행 코드 조각을 전달하는 `ContextV2` 가 더 적합하다.

# 템플릿 콜백 패턴

`ContextV2` 는 변하지 않는 템플릿 역할을 한다. 그리고 변하는 부분은 파라미터로 넘어온 `Strategy` 의 코드를 실행해서 처리한다. 

이렇게 다른 코드의 인수로서 넘겨주는 실행 가능한 코드를 콜백(callback)이라 한다.

**콜백 정의**

프로그래밍에서 콜백(callback) 또는 콜애프터 함수(call-after function)는 다른 코드의 인수로서 넘겨주는 실행 가능한 코드를 말한다. 

콜백을 넘겨받는 코드는 이 콜백을 필요에 따라 즉시 실행할 수도 있고, 아니면 나중에 실행할 수도 있다. (위키백과 참고)

쉽게 이야기해서 `callback` 은 코드가 호출( `call` )은 되는데 코드를 넘겨준 곳의 뒤( `back` )에서 실행된다는 뜻이다. 

`ContextV2` 예제에서 콜백은 `Strategy` 이다.

여기에서는 클라이언트에서 직접 `Strategy` 를 실행하는 것이 아니라, 클라이언트가 `ContextV2.execute(..)` 를 실행할 때 `Strategy` 를 넘겨주고, `ContextV2` 뒤에서 `Strategy` 가 실행된다.

**자바 언어에서 콜백**

자바 언어에서 실행 가능한 코드를 인수로 넘기려면 객체가 필요하다. 

자바8부터는 람다를 사용할 수 있다. 

자바 8 이전에는 보통 하나의 메소드를 가진 인터페이스를 구현하고, 주로 익명 내부 클래스를 사용했다. 최근에는 주로 람다를 사용한다.

**템플릿 콜백 패턴**

스프링에서는 `ContextV2` 와 같은 방식의 전략 패턴을 템플릿 콜백 패턴이라 한다. 

전략 패턴에서 `Context` 가 템플릿 역할을 하고, `Strategy` 부분이 콜백으로 넘어온다 생각하면 된다.

참고로 템플릿 콜백 패턴은 GOF 패턴은 아니고, 스프링 내부에서 이런 방식을 자주 사용하기 때문에, 스프링 안에서만 이렇게 부른다. 

전략 패턴에서 템플릿과 콜백 부분이 강조된 패턴이라 생각하면 된다.

스프링에서는 `JdbcTemplate` , `RestTemplate` , `TransactionTemplate` , `RedisTemplate` 처럼 다양한 템플릿 콜백 패턴이 사용된다.

스프링에서 이름에 `XxxTemplate` 가 있다면 템플릿 콜백 패턴으로 만들어져 있다 생각하면 된다.

<img width="687" alt="Screenshot 2024-10-09 at 17 18 33" src="https://github.com/user-attachments/assets/fdd6a571-eb10-4bea-981e-da33107f4dd2">

### 정리

지금까지 우리는 변하는 코드와 변하지 않는 코드를 분리하고, 더 적은 코드로 로그 추적기를 적용하기 위해 고군분투 했다.

템플릿 메서드 패턴, 전략 패턴, 그리고 템플릿 콜백 패턴까지 진행하면서 변하는 코드와 변하지 않는 코드를 분리했다. 

그리고 최종적으로 템플릿 콜백 패턴을 적용하고 콜백으로 람다를 사용해서 코드 사용도 최소화 할 수 있었다.

**한계**

그런데 지금까지 설명한 방식의 한계는 아무리 최적화를 해도 결국 로그 추적기를 적용하기 위해서 원본 코드를 수정해 야 한다는 점이다. 

클래스가 수백개이면 수백개를 더 힘들게 수정하는가 조금 덜 힘들게 수정하는가의 차이가 있을 뿐, 본질적으로 코드를 다 수정해야 하는 것은 마찬가지이다.

개발자의 게으름에 대한 욕심은 끝이 없다. 

수 많은 개발자가 이 문제에 대해서 집요하게 고민해왔고, 여러가지 방향으로 해결책을 만들어왔다. 

지금부터 원본 코드를 손대지 않고 로그 추적기를 적용할 수 있는 방법을 알아보자.

그러기 위해서 프록시 개념을 먼저 이해해야 한다.

**참고**

지금까지 설명한 방식은 실제 스프링 안에서 많이 사용되는 방식이다. 

`XxxTemplate` 를 만나면 이번에 학습한 내용을 떠올려보면 어떻게 돌아가는지 쉽게 이해할 수 있을 것이다.













