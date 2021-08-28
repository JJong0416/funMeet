package com.funmeet.modules.club;

import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountFactory;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.AccountService;
import com.funmeet.modules.account.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class ClubSettingsControllerTest{


    @Autowired private AccountFactory accountFactory;
    @Autowired private ClubFactory clubFactory;
    @Autowired private AccountRepository accountRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private AccountService accountService;

    @BeforeEach
    void beforeEach(){
        /* UserDetails Account 생성 */
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("jongchan");
        signUpForm.setPassword("12345678");
        signUpForm.setEmail("jjong@email.com");
        accountService.processSignUpAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 수정 - 실패 (권한이 없는 사람이 접근할 때)")
    void updateDescriptionForm_wrong() throws Exception {
        Account joinAccount = accountFactory.createNewAccount("account");
        Club club = clubFactory.createNewClub("url",joinAccount);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 수정폼 - 성공 (관리자가 접근할 때)")
    void updateDescriptionForm_right() throws Exception {
        Account managerAccount = accountRepository.findByNickname("jongchan");
        Club club = clubFactory.createNewClub("url",managerAccount);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/description"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("account"));

    }

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 소개 수정 - 실패")
    void updateDescription_wrong() throws Exception {
        Account managerAccount = accountRepository.findByNickname("jongchan");
        Club club = clubFactory.createNewClub("url",managerAccount);


        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "") // NotBlank
                .param("fullDescription", "긴 소개 체크")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 수정 - 성공")
    void updateDescription_right() throws Exception {
        Account managerAccount = accountRepository.findByNickname("jongchan");
        Club club = clubFactory.createNewClub("url",managerAccount);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "짧은 소개 체크")
                .param("fullDescription", "긴 소개 체크")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message","성공"))
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/settings/description"));
    }


}