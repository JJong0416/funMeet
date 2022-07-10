# 테스트

# 뻔(Fun)하지만, 뻔하지 않은 모음. FunMeet

### *지역 발전을 위한 취미 장려 플랫폼 (21.08 ~ 22.03)*

**Server** : [www.funmmet.shop](http://www.funmmet.shop/)

**Docs** : [https://jjongdev.notion.site/FunMeet-Docs-c4cb032923504a0f8b07de84fbb94c4d](https://www.notion.so/FunMeet-Docs-c4cb032923504a0f8b07de84fbb94c4d)

**FunMeet**은 지역과 취미를 수집해 관심 있는 사람들끼리 모임을 즐길 수 있도록 하는 **플랫폼**입니다.

서비스를 이용하는 어떤 누구도 모임(Club)을 만들 수 있으며, 모임 안에서 만남(Meeting)을 주선할 수 있습니다. 또한, 설정된 지역 정보와 취미를 가진 모임을 홍보해주는 것뿐만 아니라, 서로 만남을 주선할 수 있도록 해주는 다양한 서비스를 제공해주는 웹 애플리케이션 서비스입니다.

![https://user-images.githubusercontent.com/73544708/135741015-8c10ceef-6632-4327-9c4f-4dc5de54e28c.PNG](https://user-images.githubusercontent.com/73544708/135741015-8c10ceef-6632-4327-9c4f-4dc5de54e28c.PNG)

![https://user-images.githubusercontent.com/73544708/135741018-20f91b92-d3dd-4d17-950d-9cd9a3ffd6e4.PNG](https://user-images.githubusercontent.com/73544708/135741018-20f91b92-d3dd-4d17-950d-9cd9a3ffd6e4.PNG)

## 개발 환경

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/9698b174-3983-4ff2-b054-993881f61b3c/Untitled.png)

## 프로젝트 구조

![https://user-images.githubusercontent.com/73544708/136200814-8787704c-d225-4306-a8f0-3dcb2e873e0e.PNG](https://user-images.githubusercontent.com/73544708/136200814-8787704c-d225-4306-a8f0-3dcb2e873e0e.PNG)

- 프로젝트 설계를 하며 패키지 전략을 **레이어** 우선으로 할 지, **모듈** 우선으로 할 지 고민을 했다.
- **모듈** 우선 패키지 전략으로 개발했을 때 생기는 문제 ( **순환참조** 발생, **중복코드** 발생)을 **테스트 코드**로 잡아주고, 모듈 단위로 기능을 이식할 수 있도록 설계하고 만들었다.

## DB ERD

![https://user-images.githubusercontent.com/73544708/136201176-6b48577e-c2ec-43d0-b4ac-5ebf929bb1cd.png](https://user-images.githubusercontent.com/73544708/136201176-6b48577e-c2ec-43d0-b4ac-5ebf929bb1cd.png)

## 부가 **기능 구현**

- 카카오 로그인(OAuth 2.0)
- 모임 홍보 알람 서비스(SMTP **asynchronous**)
- 인증 이메일 서비스

## 📢프로젝트를 진행하며 리팩토링 한 부분

### 1. **Mockito를 이용한 테스트코드 리팩토링(기한: 22.02.15 ~ 22.03.15)**

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a5a7f2fa-3cff-4d91-b3e3-2396065b03d0/Untitled.png)

- Mockito를 사용함으로써, DB를 사용할 필요없이, 스터빙(Stubbing) 된 데이터를 가져오
기 때문에 **테스트코드의 시간을 줄여**주는 것 뿐만 아니라 **리소스를 절약**할 수 있다.
- 개발자가 **객체의 행동까지 조정**함으로써, 좀 **더 유연한 테스트코드를 작성**할 수 있게 됩
니다.

### 2. **이메일 서비스 전략패턴으로 [리팩토링](https://www.notion.so/7a09e87017364bc78c6b9b0a128917a0)(기한 : 22.02.08 ~ 22.02.10)**

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/239873af-d782-4955-a156-f8b210e7a57f/Untitled.png)

- 전략패턴의 경우, **클라이언트가 전략을 생성해 전략을 실행할 컨텍스트에 주입**해주기 때
문에 의존성을 반대로 가지게 할 수 있다.
- 이렇게 클라이언트가 직접 주입해주게 된다면, **유지보수의 이점**과 **유연한 테스트코드**를
작성할 수 있게 되었다.

### 3. **Spring Controller-Service 책임 다시 나누기(기한: 22.01.02 ~ 22.01.06)**

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a64b74df-59dc-434b-95eb-3f278ebdfea9/Untitled.png)

- 책임(Layer)을 분리함으로써, **컴포넌트들의 서로의 의존 계층 관계를 깔끔하게 유지**할 수 있
다.

## 🐳 프로젝트를 진행하며 겪은 이슈 처리방식과 생각

### 1. N+1 Selector 이슈와 Query 최적화

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/95ab7ece-2f56-424f-a906-89cf7311d50c/Untitled.png)

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8955532f-0c0b-4617-ab70-e713f73aa2dc/Untitled.png)

- AOP를 통해 각 쿼리들과 메소드의 시간을 체크해 가며, 어느 부분에 병목현상이 있었는 지
확인할 수 있다.
- 그러던 중, 설계했던 부분의 쿼리와 다른 쿼리를 DB에 찌르는 부분을 발견
- **fetch join을 통해 추가적인 쿼리 발생(N+1 Select)** 문제를 해결하였으며, 각 쿼리들의 최적
쿼리를 작성함으로 성능 이슈에 대해 최소화 시켰다.

```java
@Override
    public Page<Club> findByKeyword(String keyword, Pageable pageable) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.hobby.any().title.containsIgnoreCase(keyword))
                .or(club.city.any().krCity.containsIgnoreCase(keyword)))
                .leftJoin(club.hobby, QHobby.hobby).fetchJoin()
                .leftJoin(club.city, QCity.city).fetchJoin()
                .leftJoin(club.members, QAccount.account).fetchJoin()
                .distinct();

        JPQLQuery<Club> pageableQuery = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);
        QueryResults<Club> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

```

### 2. SMPT Mail Delivery Speed

![https://user-images.githubusercontent.com/73544708/136201667-3ade7a9d-5393-40d1-8387-4ce661848fa8.PNG](https://user-images.githubusercontent.com/73544708/136201667-3ade7a9d-5393-40d1-8387-4ce661848fa8.PNG)

- 특정 이미지를 메일을 통해 발송하는 부분에서 많은 시간과 리소스를 잡아먹는 것을 확인
- 홍보 메세지를 **웹 애플리케이션으로 보내주는 과정에서 기존 서비스 동작속도에 영향을 주는 것을 확인**하고, **기존 서비스 로직에서 시간을 소비하지 않고 전송할 수 있는 방법을 고민**했다.
- 그 방법 중 하나는 ThreadPool을 이용한 **비동기**(asynchronous)를 사용하였다.

```java
@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();//가용 프로세스 숫자
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor:");
        executor.initialize();
        return executor;
    }
```

### 3. 하이버네이트 MultipleBagFetchException

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4b032f41-b4f5-4312-8ad5-9e2d5dd2a556/Untitled.png)

- Data JPA를 사용 중, **`org.hibernate.loader.MultipleBagFetchException: cannot simultaneously
fetch multiple bags`** 이란 메세지가 발생했다.
- OneToMany, ManyToMany에서 한 엔티티에서 두 개 이상을 EAGER로 fetch 했을 때 발생 하였다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/14513c20-03b6-4fb1-bc92-f7e140dc7942/Untitled.png)

- LAZY로 해도 같은 문제가 발생했고, 직접 MultipleBagFetchException을 확인해보니,
BagType을 동시에 fetch 해 올 때 발생하는 예외라고 한다.
- Bag(Multiset)은 Set과 같이 순서가 없고, List와 같이 중복을 허용하는 자료구조이고, 자바
컬렉션 프레임워크에선 Bag가 없기 때문에 하이버네이트에선 List를 Bag로써 사용하고 있던
것이다.
- 문제가 발생하던 곳을 List가 아닌 Set을 통해 문제를 해결했다.

## 프로젝트 개발 시 집중했던 부분

---

### 1. 지속적인 Refactoring

![https://user-images.githubusercontent.com/73544708/136201857-e0d7206e-93cb-43c8-ab39-5e90abf6fb02.PNG](https://user-images.githubusercontent.com/73544708/136201857-e0d7206e-93cb-43c8-ab39-5e90abf6fb02.PNG)

- 개발을 진행하며, 완성된 기능은 그것으로 마무리 하는 것이 아닌, 프로젝트를 진행하며 변화하는 부분에 맞춰 리팩토링을 합니다.
- 가독성뿐만 아니라, 프로젝트 기간 동안 Service 로직을 최적화시키려고 노력했습니다.

### 2. 성능 향상

- 기술을 단순하게 쓰는 것이 아닌, 기술에 대해 학습을 하고 해당 기술에서 성능을 향상시키며 프로젝트를 진행했습니다.

### 3. 서비스 완성 및 배포

- 프로젝트의 프론트부터 백엔드 그리고 인프라까지 Product의 한 사이클을 돌아보며, 개발에 대한 전반적인 개념과 구조에 대해 뚜렷하게 알게 되었다.
