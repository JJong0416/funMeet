# 뻔(Fun)하지만, 뻔하지 않은 모음. FunMeet


### *지역 발전을 위한 취미 장려 플랫폼 (21.07 ~ 21.09)*

**Github** : [https://github.com/JJong0416/funMeet](https://github.com/JJong0416/funMeet)

**Server** : [www.funmmet.shop](http://www.funmmet.shop)

**Docs** : [https://jjongdev.notion.site/FunMeet-Docs-c4cb032923504a0f8b07de84fbb94c4d](https://www.notion.so/FunMeet-f3049227bc0140e899218016fae502f6)

**FunMeet**은 지역과 취미를 수집해 관심 있는 사람들끼리 모임을 즐길 수 있도록 하는 플랫폼입니다.

서비스를 이용하는 어떤 누구도 모임(Club)을 만들 수 있으며, 모임 안에서 만남(Meeting)을 주선할 수 있습니다. 또한, 설정된 지역 정보와 취미를 가진 모임을 홍보해주는 것뿐만 아니라, 서로 만남을 주선할 수 있도록 해주는 웹 애플리케이션 서비스입니다.

인프라에서부터 백엔드 서버까지 설계를 진행했으며, 전반적인 서버 개발과 인프라 설정을 집중적으로 진행했습니다.

## System A**rchitecture**

![image](https://user-images.githubusercontent.com/73544708/195134069-0ecf3e05-564b-41cd-8cbd-6d29f47ea458.png)

## ERD(Entity Relationship Diagram)

![image](https://user-images.githubusercontent.com/73544708/195134566-6fb9fc4e-42cb-4211-ab98-56f1fc113846.png)

## Stack


![image](https://user-images.githubusercontent.com/73544708/195135485-b723f513-3168-4c8d-8fd5-1312b49381d9.png)

## Project Package **Strategy**

![image](https://user-images.githubusercontent.com/73544708/195134859-2a01fab4-1d4b-40d0-aa40-61f7d316c413.png)

- 프로젝트 설계를 하며 패키지 전략을 **레이어** 우선으로 할 지, **모듈** 우선으로 할 지 고민.
- **모듈** 우선 패키지 전략으로 개발했을 때 순환참조 발생 및 중복코드 이슈 발생.
    - 이슈를 **테스트 코드**로 잡아주고, 모듈 단위로 기능을 이식할 수 있도록 설계 후 제작.

## Project **Core Features**

1. **세션방식**의 로그인 방식 & **Oauth2.0**을 이용한 소셜 로그인
2. **인터셉터**를 통해 상태변화 후, 모임 홍보 및 알림 서비스 **비동기 처리**
3. **동시성**을 고려한 모임 가입 및 선착순 방식

# `프로젝트를 진행하며 개선에 노력한 부분😊`


### **1. Mockito를 이용한 테스트코드 리팩토링(기한: 22.02.15 ~ 22.03.15)**

![image](https://user-images.githubusercontent.com/73544708/195135302-d30281f2-d4e4-4022-a9c0-f4c1587ab3b9.png)

- 프로젝트를 진행하면서, 테스트코드가 증가하고, 빌드&실행 시간이 점점 늘어나게 됨.
    - 그 중 하나가 **네트워크나 DB 연결 등 외부의 요인의 영향을 많이 받는 것**을 확인.
- 먼저, TDD 강의와 [**코드리뷰**](https://github.com/JJong0416/BabyTDD/commits/JJong0416-step03)를 받으며 올바른 테스트코드 작성방법을 학습
- 대부분의 테스트코드에 **Mockito**를 적용
    - Mockito를 통해 개발자가 객체의 행동을 정해줄 수 있으며, 유연한 테스트코드를 작성
    - Mockito를 적용하면서, **최소 25%, 최대 35%까지 성능을 향상**.

### **2. 이메일 서비스 전략패턴으로 리팩토링(기한 : 22.02.08 ~ 22.02.10)**

![image](https://user-images.githubusercontent.com/73544708/195135892-ba1319c0-3db8-4928-9824-fc662e73f743.png)

- 리팩토링과 확장을 진행하면서, **필요한 부분 이외에 많은 다른 부분에서 변경이 발생**
- 이를 보완해주기 위해 올바른 설계 방식을 공부할 필요를 느꼈고, [디자인 패턴](https://www.notion.so/7a09e87017364bc78c6b9b0a128917a0)을 공부
- 이메일 서비스에 클라이언트가 전략을 생성해 전략을 주입해주는 **전략패턴**을 적용
    - 전략패턴의 경우, 의존성을 반대로 가질 수 있기 때문에 **카카오 이메일 서비스 등 생기게 되면 코드의 변화가 극적으로 줄어듬**.

### 3. 올바른 Spring MVC Layer 책임 나누기 (기한: 22.01.02 ~ 22.01.06)

![image](https://user-images.githubusercontent.com/73544708/195136672-c79c61aa-736e-44ee-bf1a-8e50dc91cf11.png)

- 책임(Layer)을 분리함으로써, **컴포넌트들의 서로의 의존 계층 관계를 깔끔하게 유지**할 수 있다.

# `프로젝트를 진행하며 이슈발생과 처리과정😊`


### 1. N+1 Selector 이슈와 Query 최적화

![image](https://user-images.githubusercontent.com/73544708/195137361-c476dfe5-38da-4204-8025-574e5c2a715a.png)

- **AOP를 통해 성능을 분석**하던 도중, 특정 메소드에 예상보다 많은 질의문이 발생하는 것을 포착
- 문제를 확인해보니, 연관관계에 있어 예상치 못한 엔티티를 추가적으로 불러오는 부분 발견.
    - 설계 당시, 발견하지 못했던 부분이며 **엔티티 설계방법에 있어서는 문제가 없다는 결론 도출**
    - **Fetch Join**을 통해 최적화 쿼리를 작성하였고, **EntityGraph**를 통해 **최대 50%까지 성능 개선**

### 2. 하이버네이트 **MultipleBagFetchException**

![image](https://user-images.githubusercontent.com/73544708/195137584-50a13fce-ce33-4a3b-9826-d09d49ddd3c1.png)

- Spring Data JPA를 사용중, **`org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags`** 에러 발생
- **`OneToMany`**, **`ManyToMany`** 관계에서 한 엔티티가 두 개 이상의 엔티티를  패치 조인을 했을 경우, 발생하는 에러라는 것을 파악
    - 결론적으로 BagType을 동시에 패치 조인을 했을 때 발생하는 예외
    - Bag(Multiset)은 Set과 같이 순서가 없고, List와 같이 중복을 허용하는 자료구조인 걸 확인.
    - 순서와 상관이 없는 필드였기 때문에, List에서 Set으로 변경하면서 문제 해결
