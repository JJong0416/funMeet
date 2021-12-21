package com.funmeet.modules.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface AccountRepository extends JpaRepository<Account, Long> , QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByNickname(String id);

    Account findByEmail(String email);

    Account findByKakaoEmailAndKakaoTokenVerifiedTrue(String kakaoEmail);

    @EntityGraph(attributePaths = {"hobby","city"})
    Account findAccountWithHobbyAndCityById(Long id);
}
