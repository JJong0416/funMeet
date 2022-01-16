package com.funmeet.modules.club;

import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.AccountService;
import com.funmeet.modules.account.form.SignUpForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@MockMvcTest
public class ClubControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired ClubRepository clubRepository;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void initEach(){
        final SignUpForm signUpForm = SignUpForm.builder()
                .nickname("account001")
                .password("password001")
                .email("test@test.com")
                .build();
        accountService.processSignUpAccount(signUpForm);
    }

    @Test
    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 등록 폼")
    void 정상적인_회원이_모임을_만들기위해_모임폼을_연다() throws Exception {
        // given
        final ResultActions resultActions;

        // when
        resultActions = mockMvc.perform(get("/create-club"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("clubForm"));
    }

    @Test
    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 생성 - 성공")
    void 모임을_만들기위해_폼에_맞는_양식을_작성한후_모임을_생성한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        final ResultActions resultActions;
        final String url = "url001";

        // when
        resultActions = mockMvc.perform(post("/create-club")
                .param("title", "테스트모임")
                .param("clubPath",url)
                .param("shortDescription", "짧게 보기 체크")
                .param("fullDescription", "상세 보기 체크")
                .with(csrf()));

        // then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/url001"));

        Club club = clubRepository.findByClubPath("url001");
        assertNotNull(club);
        assertTrue(club.getManagers().contains(account));
    }

    @Test
    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 생성 - 실패")
    void 모임을_만들기위해_폼에_틀린_양식을_작성한후_모임을_생성후_실패한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        final ResultActions resultActions;
        final String url = "^뀆@룯";

        // when
        resultActions = mockMvc.perform(post("/create-club")
                .param("title", "테스트모임")
                .param("clubPath",url)
                .param("shortDescription", "짧게 보기 체크")
                .param("fullDescription", "상세 보기 체크")
                .with(csrf()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubForm"))
                .andExpect(model().attributeExists("account"));
        Club club = clubRepository.findByClubPath(url);
        assertNull(club);
    }

    @Test
    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 조회")
    void 모임을_만든후_모임장만_모임을_조회를_할수있다() throws Exception {

    }
}
