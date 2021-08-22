package com.funmeet.controller;

import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ClubSettingsControllerTest extends ClubControllerTest{

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 수정 - 실패 (권한이 없는 사람이 접근할 때)")
    void updateDescriptionForm_wrong() throws Exception {
        Account joinAccount = createNewAccount("account");
        Club club = createNewClub("url",joinAccount);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 수정폼 - 성공 (관리자가 접근할 때)")
    void updateDescriptionForm_right() throws Exception {
        Account managerAccount = accountRepository.findByNickname("jongchan");
        Club club = createNewClub("url",managerAccount);

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
        Club club = createNewClub("url",managerAccount);


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
        Club club = createNewClub("url",managerAccount);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "짧은 소개 체크")
                .param("fullDescription", "긴 소개 체크")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message","성공"))
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/settings/description"));
    }


}