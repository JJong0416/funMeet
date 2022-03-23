package com.funmeet.modules.club;

import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountFactory;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.club.form.ClubForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.funmeet.modules.club.ClubFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
@ExtendWith(MockitoExtension.class)
public class ClubControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired // WithUserDetails를 사용하려면 어쩔 수 없이 사용해야함.
    AccountRepository accountRepository;
    @Autowired
    ClubRepository clubRepository; // form을 통해 MockMVC 테스트 Bean
    @Autowired
    ClubService clubService;

    static String initNickname = "account001";

    @BeforeEach
    void initEach() {
        final Account account = AccountFactory.createSuccessAccount();
        accountRepository.save(account);
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 등록 폼")
    void 정상적인_회원이_모임을_만들기위해_모임폼을_연다() throws Exception {
        final ResultActions resultActions = mockMvc.perform(get("/create-club"));

        resultActions.andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("clubForm"));
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 생성 - 성공")
    void 모임을_만들기위해_폼에_맞는_양식을_작성한후_모임을_생성한다() throws Exception {
        // given
        final Account account = accountRepository.findByNickname(initNickname).orElseThrow();

        // when
        final ResultActions resultActions = mockMvc.perform(post("/create-club")
                .param("title", CORRECT_CLUB_TITLE)
                .param("clubPath", CORRECT_CLUB_PATH)
                .param("shortDescription", CORRECT_CLUB_SHORT_DESCRIPTION)
                .param("fullDescription", CORRECT_CLUB_FULL_DESCRIPTION)
                .with(csrf()));

        // then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + CORRECT_CLUB_PATH));

        Club findClub = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        assertThat(findClub.getTitle()).isEqualTo(CORRECT_CLUB_TITLE);
        assertThat(findClub.getManagers()).contains(account);
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 생성 - 실패")
    void 모임을_만들기위해_폼에_틀린_양식을_작성한후_모임을_생성후_실패한다() throws Exception {
        // given
        final Account account = accountRepository.findByNickname(initNickname).orElseThrow();
        final String WRONG_URL = "^뀆@룯";

        // when
        final ResultActions resultActions = mockMvc.perform(post("/create-club")
                .param("title", CORRECT_CLUB_TITLE)
                .param("clubPath", WRONG_URL)
                .param("shortDescription", CORRECT_CLUB_SHORT_DESCRIPTION)
                .param("fullDescription", CORRECT_CLUB_FULL_DESCRIPTION)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION) // Account002
    @DisplayName("모임 조회")
    void 모임을_만든후_모임을_조회를_할수있다() throws Exception {
        // given
        Account account = accountRepository.findByNickname(initNickname).orElseThrow(); // 클럽장
        ClubForm clubForm = ClubFactory.createCorrectClubForm();
        Club club = clubService.createNewClub(account, clubForm);
        club.addManager(account);
        clubRepository.save(club);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/club/test-url001"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("club/page"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }
}
