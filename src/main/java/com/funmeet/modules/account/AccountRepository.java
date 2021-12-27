package com.funmeet.modules.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly=true)
public interface AccountRepository extends JpaRepository<Account, Long> , QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Transactional
    void removeAllHobbyAndCityByEmail(String email);

    Optional<Account> findByNickname(String id);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByKakaoEmailAndKakaoTokenVerifiedTrue(String kakaoEmail);

    @EntityGraph(attributePaths = {"hobby","city"})
    Account findAccountWithHobbyAndCityById(Long id);
}
