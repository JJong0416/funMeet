package com.funmeet.modules.account;

import com.funmeet.modules.club.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> , QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByNickname(String id);

    Account findByEmail(String email);

    Account findByKakaoEmailAndKakaoTokenVerifiedTrue(String kakaoEmail);

}
