package com.funmeet.modules.club;

import com.funmeet.modules.account.AdaptAccount;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.Club;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClubServiceTest {

    Club club;
    Account account;
    AdaptAccount adaptAccount;

    @BeforeEach
    void beforeEach() {
        club = new Club();
        account = new Account();
        account.setNickname("jjong0416");
        account.setPassword("as1234");
        account.setEmail("as@as");
        adaptAccount = new AdaptAccount(account);
    }

    @DisplayName("모임을 오픈했으며 모임원을 모집중이며, 이미 멤버나 관리자가 아니라면 스터디 가입 가능 체크")
    @Test
    void isJoinable() {
        club.setPublished(true);
        club.setRecruiting(true);

        assertTrue(club.isJoinable(adaptAccount));
    }

    @DisplayName("모임을 오픈했고 인원 모집 중이더라도, 관리자는 가입을 할 수 없다.")
    @Test
    void isJoinable_false_manager() {
        club.setPublished(true);
        club.setRecruiting(true);
        club.addManager(account);

        assertFalse(club.isJoinable(adaptAccount));
    }

    @DisplayName("모임을 오픈했고 인원 모집 중이더라도, 모임원은 가입을 할 수 없다.")
    @Test
    void isJoinable_false_member() {
        club.setPublished(true);
        club.setRecruiting(true);
        club.addMember(account);
        assertFalse(club.isJoinable(adaptAccount));
    }
}