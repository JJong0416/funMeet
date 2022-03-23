package com.funmeet.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class AccountSettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void initEach() {
        final Account account = AccountFactory.createSuccessAccount();
        accountRepository.save(account);
    }

    /* WithUserDetails은 지정한 사용자 이름으로 계정을 조회한 후,
     *  UserDetails 객체를 조회하여 보안 컨텍스트를 로드하게 됩니다.*/
    @DisplayName("프로필 수정 - 초기 폼")
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void 정상적인_계정의_프로필을_수정하는_폼을_연다() throws Exception {

        final ResultActions resultActions = mockMvc.perform(get("/settings/profile"));

        resultActions.andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 짧은 소개 수정")
    @Test
    void 정상적인_계정의_프로필을_열어서_올바른_짧은소개와_프로필이미지를_바꾼다() throws Exception {
        // given
        final String testShortBio = "간단한 자기소개를 해보겠습니다";

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/profile")
                .param("shortBio", testShortBio)
                .with(csrf()));

        // then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attribute("message", "성공"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
                .andExpect(view().name("settings/hobby"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("hobby"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 취미추가")
    @Test
    void 정상적인_계정의_올바른_취미를_추가한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        HobbyForm hobbyForm = new HobbyForm("취미1");
        Hobby hobby = Hobby.builder().title("취미1").build();

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/hobby/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        // then
        assertThat(account.getHobby().size()).isEqualTo(1);
        assertThat(account.getHobby().stream().map(Hobby::getTitle).collect(Collectors.toList()).get(0)).isEqualTo("취미1");
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 취미삭제 성공")
    @Test
    void 정상적인_계정의_취미를_삭제한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        HobbyForm hobbyForm = new HobbyForm("취미1");
        Hobby hobby = Hobby.builder().title("취미1").build();
        account.getHobby().stream().map(Hobby::getTitle).collect(Collectors.toList());

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/hobby/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
        assertThat(account.getHobby()).isEmpty();
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 관심도시 폼")
    @Test
    void 정상적인_계정에_관심도시를_추가하는_폼을_연다() throws Exception {

        final ResultActions resultActions = mockMvc.perform(get("/settings/location"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("settings/location"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("city"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 도시추가")
    @Test
    void 정상적인_계정의_올바른_관심도시를_추가한다() throws Exception {
        // given
        final Account account = accountRepository.findByNickname("account001").orElseThrow();
        final City city = City.builder().enCity("Seoul").krCity("서울전체").build();
        final CityForm cityForm = CityForm.builder().cityName("Seoul(서울전체)").build();

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/location/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cityForm))
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
        assertThat(account.getCity().size()).isEqualTo(1);
        assertThat(account.getCity().stream().map(City::getKrCity).collect(Collectors.toList()).get(0)).isEqualTo("서울전체");
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 도시삭제")
    @Test
    void 정상적인_계정의_올바른_관심도시를_삭제한다() throws Exception {
        // given
        final Account account = accountRepository.findByNickname("account001").orElseThrow();
        final City city = City.builder().krCity("테스트시").enCity("testCity").build();
        final CityForm cityForm = CityForm.builder().cityName("testCity(테스트시)").build();

        // when
        final ResultActions resultActions =
                mockMvc.perform(post("/settings/location/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityForm))
                        .with(csrf()));

        // then
        assertThat(account.getCity().size()).isEqualTo(0);
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 알림폼")
    @Test
    void 정상적인_계정이_알림폼을_연다() throws Exception {
        final ResultActions resultActions = mockMvc.perform(get("/settings/notification"));

        resultActions.andExpect(status().isOk())
                .andExpect(view().name("settings/notification"))
                .andExpect(model().attributeExists("notification"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 알림 수정")
    @Test
    void 정상적인_계정이_알림설정을_해서_성공한다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();

        // when
        assertFalse(account.isMeetCreatedByEmail());
        final ResultActions resultActions = mockMvc.perform(post("/settings/notification")
                .param("meetCreatedByEmail", String.valueOf(true))
                .param("meetCreatedByWeb", String.valueOf(true))
                .param("meetEnrollmentResultByEmail", String.valueOf(true))
                .param("meetEnrollmentResultByWeb", String.valueOf(true))
                .param("meetUpdateByEmail", String.valueOf(true))
                .param("meetUpdatedByWeb", String.valueOf(true))
                .with(csrf()));

        // then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notification"));
        assertTrue(account.isMeetEnrollmentResultByEmail());
        assertTrue(account.isMeetCreatedByEmail());
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 계정 폼")
    @Test
    void 정상적인_계정이_프로필수정의_계정폼을_연다() throws Exception {
        final ResultActions resultActions = mockMvc.perform(get("/settings/account"));

        resultActions.andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 닉네임 변경 성공")
    @Test
    void 정상적인_계정이_닉네임을_수정하고_성공적으로_바꾼다() throws Exception {
        // given
        Account account = accountRepository.findByNickname("account001").orElseThrow();
        final String newNickname = "올바른닉네임";

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/account")
                .param("nickname", newNickname)
                .with(csrf()));

        // then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        Account changeNicknameAccount = accountRepository.findByNickname(newNickname).orElseThrow();
        assertThat(changeNicknameAccount.getNickname()).isEqualTo(newNickname);
    }

    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 계정 삭제")
    @Test
    void 정상적인_계정이_계정을_삭제한다() throws Exception {
        // given
        final Account account = accountRepository.findByNickname("account001").orElseThrow();

        // when
        final ResultActions resultActions = mockMvc.perform(post("/settings/delete")
                .with(csrf()));

        // then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertEquals(Optional.empty(), accountRepository.findByNickname("account001"));
    }
}
