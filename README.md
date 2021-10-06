# 뻔(Fun)하지만, 뻔하지 않은 모음. FunMeet


### *지역 발전을 위한 취미 장려 플랫폼 (21.07 ~ 21.09)*

**Server** : [www.funmmet.shop](http://www.funmmet.shop)

**Docs** : [https://jjongdev.notion.site/FunMeet-Docs-c4cb032923504a0f8b07de84fbb94c4d](https://www.notion.so/FunMeet-Docs-c4cb032923504a0f8b07de84fbb94c4d)

**FunMeet**은 지역과 취미를 수집해 관심 있는 사람들끼리 모임을 즐길 수 있도록 하는 플랫폼(RESTful API) 프로젝트입니다.

서비스를 이용하는 어떤 누구도 모임(Club)을 만들 수 있으며, 모임 안에서 만남(Meeting)을 주선할 수 있습니다. 또한, 설정된 지역 정보와 취미를 가진 모임을 홍보해주는 것뿐만 아니라, 서로 만남을 주선할 수 있도록 해주는 웹 애플리케이션 서비스입니다.

인프라에서부터 백엔드 서버까지 설계를 진행했으며, 전반적인 서버 개발과 인프라 설정을 집중적으로 진행했습니다.

![ppt_1](https://user-images.githubusercontent.com/73544708/135741015-8c10ceef-6632-4327-9c4f-4dc5de54e28c.PNG)
---
![ppt_2](https://user-images.githubusercontent.com/73544708/135741018-20f91b92-d3dd-4d17-950d-9cd9a3ffd6e4.PNG)

## 개발 환경
> **Back-end**
> 
- Spring Boot
- Spring Data JPA
- Spring Security
- QueryDSL
- OAuth 2.0
- Gradle

> **Front-end**
> 
- Bootstrap v4.4.1
- jQuery v3.4.1
- Thymeleaf
- sweetAlert v2.1.2

> **Test**
> 
- Junit5
- docker (testContainer)

> **Database**
> 
- PostgreSQL

> **Infra**
> 
- Amazon EC2
- Amazon ROUTE53
- Aamzon RDS
- NGINX
   
   
## 프로젝트 구조
![hierarchy_chap](https://user-images.githubusercontent.com/73544708/136200814-8787704c-d225-4306-a8f0-3dcb2e873e0e.PNG)

- 프로젝트 설계를 하며 패키지 전략을 **레이어** 우선으로 할 지, **모듈** 우선으로 할 지 고민을 했다.
- **모듈** 우선 패키지 전략으로 개발했을 때 생기는 문제 ( **순환참조** 발생, **중복코드** 발생)을 **테스트 코드**로 잡아주고, 모듈 단위로 기능을 이식할 수 있도록 설계하고 만들
   
## DB ERD
![ERD](https://user-images.githubusercontent.com/73544708/136201176-6b48577e-c2ec-43d0-b4ac-5ebf929bb1cd.png)
   
## 부가 **기능 구현**
- 카카오 로그인(OAuth 2.0)
- 모임 홍보 알림 서비스(SMTP **asynchronous**)
- 자동 로그인(jdbc Token & Cookie)
- 인증 이메일 서비스

## 📢프로젝트 Issue 및 해결책

- 발생했던 많은 이슈 중, 핵심적인 이슈를 가지고 왔다.

### 1. JPA N+1 Select

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

- 모임을 검색하는 기능을 구현하는 도중, **view**에 여러가지 정보를 띄어주기 위해 여러가지 Entity를 불러와야 했다.
- 그렇지만, 가져오는 데이터의 연관 관계로 인해 조회된 데이터 개수 N만큼 연관관계의 조회 쿼리가 추가로 발생해 데이터를 불러오게 되었다.
- 해결방안으로, Fetch join을 통해 N+1 Select 문제를 해결하였다.
![하기전](https://user-images.githubusercontent.com/73544708/136201400-385d73f7-70ee-447c-b8b0-99160f34253d.PNG)
![하고나서_수행시간](https://user-images.githubusercontent.com/73544708/136201449-ded9ea42-a520-4873-9687-519d3ea1899e.PNG)

- Fetch Join을 통해 추가로 발생하는 쿼리가 사라졌기 때문에, 성능적인 부분에서 수행 시간이 더 빨라졌다는 것을 알 수가 있다.

### 2. SMPT Mail Delivery Speed

```java
@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors(); // 가용 프로세스 숫자
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor:");
        executor.initialize();
        return executor;
    }
```
![회원가입](https://user-images.githubusercontent.com/73544708/136201667-3ade7a9d-5393-40d1-8387-4ce661848fa8.PNG)
- SMPT가 **HTML을 그려 보내주는 시간이 상당히 오래** 걸린다.
- 홍보 메세지를 웹 애플리케이션으로 보내주는 과정에서 기존 서비스 동작속도에 영향을 주는 것을 확인하고, 기존 서비스 로직에서 시간을 소비하지 않고 전송할 수 있는 방법을 고민했다.
- 그 방법 중 하나는 ThreadPool을 이용한 
**비동기**(asynchronous)를 사용하였다. ****

### 3. Query Performance

```java
@EntityGraph(attributePaths = {"hobby","city"})
    Account findAccountWithHobbyAndCityById(Long id);

@EntityGraph(attributePaths = {"managers","members"})
    Club findClubWithManagersAndMembersById(Long id);
```

- 프로젝트 초반에 JPA를 단순하게 사용했기 때문에, 어떤 Query들이 추가적으로 발생하는지 모르고 사용하였다.
- JPA를 사용하기 이전에, 연관 관계를 무시하고 작업했기 때문에 발생했던 문제로 JPA라는 기술에 대해 조금 더 핵심적으로 공부하면서 프로젝트에 접근하였다.
   
   
## 프로젝트 개발 시 집중했던 부분

---

### 1. 지속적인 Refactoring

![끊임없는 개발](https://user-images.githubusercontent.com/73544708/136201857-e0d7206e-93cb-43c8-ab39-5e90abf6fb02.PNG)

- 개발을 진행하며, 완성된 기능은 그것으로 마무리 하는 것이 아닌, 프로젝트를 진행하며 변화하는 부분에 맞춰 리팩토링을 합니다.
- 가독성뿐만 아니라, 프로젝트 기간 동안 Service 로직을 최적화시키려고 노력했습니다.

### 2. 성능 향상

- 기술을 단순하게 쓰는 것이 아닌, 기술에 대해 학습을 하고 해당 기술에서 성능을 향상시키며 프로젝트를 진행했습니다.

### 3. 서비스 완성 및 배포

- 프로젝트의 프론트부터 백엔드 그리고 인프라까지 Product의 한 사이클을 돌아보며, 개발에 대한 전반적인 개념과 구조에 대해 뚜렷하게 알게 되었다.

   

