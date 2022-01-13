package com.funmeet.modules.account;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import com.funmeet.modules.hobby.HobbyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockMvcTest
public class AccountSettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired CityRepository cityRepository;
    @Autowired HobbyRepository hobbyRepository;
    @Autowired ObjectMapper objectMapper;

    // TODO: Layer에 맞는 테스트코드만 작성. Mockito 제대로 배우고 리팩토링 하기.

    @BeforeEach
    void initEach(){
        final SignUpForm signUpForm = SignUpForm.builder()
                .nickname("account001")
                .password("password001")
                .email("test@test.com")
                .build();
        accountService.processSignUpAccount(signUpForm);
    }

    @AfterEach
    void afterEach(){

    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정폼")
    @Test
    void 정상적인_계정의_프로필을_수정하는_폼을_연다() throws Exception {
        // given
        final ResultActions resultActions;

        // when
        resultActions = mockMvc.perform(get("/settings/profile"));

        // then
        assertNotNull(resultActions);
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 짧은 소개 수정")
    @Test
    void 정상적인_계정의_프로필을_열어서_올바른_짧은소개와_프로필이미지를_바꾼다() throws Exception {
        // given
        final String testShortBio = "간단한 자기소개를 해보겠습니다";

        // when
        final ResultActions resultActions =
                mockMvc.perform(post("/settings/profile")
                .param("shortBio", testShortBio)
                .with(csrf()));

        // then
        assertNotNull(resultActions);
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attribute("message","성공"));
        Account compareAccount = accountRepository.findByNickname("account001").orElseThrow();
        assertEquals(compareAccount.getShortBio(), testShortBio);
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 짧은 소개 수정")
    @Test
    void 정상적인_계정의_프로필을_열어서_틀린_짧은소개와_프로필이미지를_바꾼다() throws Exception {
        // given
        final String testShortBio = "<br><div>script Attack<div></br>";

        // when
        final ResultActions resultActions =
                mockMvc.perform(post("/settings/profile")
                        .param("shortBio", testShortBio)
                        .with(csrf()));
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 취미폼")
    @Test
    void 정상적인_계정의_취미를_추가하는_폼을_연다() throws Exception {
        // given
        final ResultActions resultActions;


        // when
        resultActions = mockMvc.perform(get("/settings/hobby"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("hobby"));
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 취미추가 성공")
    @Test
    void 정상적인_계정의_올바른_취미를_추가한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        HobbyForm hobbyForm = new HobbyForm("취미1");

        // when
        final ResultActions resultActions =
                mockMvc.perform(post("/settings/hobby/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()));

        // then
        resultActions
                .andExpect(status().isOk());

        Hobby hobby = hobbyRepository.findByTitle("취미1").orElseThrow();
        assertNotNull(hobby);
        assertTrue(account.getHobby().contains(hobby));
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 취미추가 실패")
    @Test
    void 정상적인_계정의_틀린_취미를_추가한다(){
        // given
        final ResultActions resultActions;

        // when

        // then
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 관심도시 폼")
    @Test
    void 정상적인_계정에_관심도시를_추가하는_폼을_연다(){
        // given
        final ResultActions resultActions;

        // when

        // then
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 도시추가 성공")
    @Test
    void 정상적인_계정의_올바른_관심도시를_추가한다(){
        // given
        final ResultActions resultActions;

        // when

        // then
    }

    @WithUserDetails(value="account001",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 도시추가 실패")
    @Test
    void 정상적인_계정의_틀린_관심도시를_추가한다(){
        // given
        final ResultActions resultActions;

        // when

        // then
    }


//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("설정 - 지역 정보 수정 폼")
//    @Test

//    @Transactional
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("설정 - 지역 정보 추가")

//    @Transactional
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("설정 - 지역 정보 추가")
//    @Test

//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("패스워드 수정 - 입력값 정상")
//    @Test

//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("알림 설정 폼")
//    @Test

//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("닉네임 수정 폼")
//    @Test

//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("닉네임 수정하기 - 입력값 정상")
//    @Test
}
