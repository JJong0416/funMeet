package com.funmeet.modules.account;

import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.mapper.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
@MockMvcTest
public class AccountControllerTest {

    @InjectMocks private AccountController accountController;

    @Mock private AccountService accountService;
    @Mock private AccountEmailService accountEmailService;
    @Mock private AccountDetailsService accountDetailsService;
    @Mock private AccountRepository accountRepository;
    @Mock private PasswordEncoder passwordEncoder;

    // API의 경우, 함수 실행을 위해 메소드가 아닌 API가 호출되므로 우리의 API 요청을 받아 전달하기 위한 별도의 객체 생성
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @DisplayName("회원가입폼 이동 성공")
    @Test
    void 정상적인_접근이_회원가입_API_호출했을때_회원가입폼을_준다() throws Exception {

        // given
        final ResultActions resultActions;

        // when
        resultActions = mockMvc.perform(get("/sign-up"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        assertThat(mvcResult).isNotNull();
    }


    @DisplayName("회원가입폼 입력 성공")
    @Test
    void 정상적인_회원가입폼을_입력해서_회원가입을_성공한다() throws Exception{

        //given
        final SignUpForm signUpForm = SignUpForm.builder() // 폼에 올바른 값을 입력하면
                .nickname("테스트계정입니다")
                .password("password")
                .email("test@test.com")
                .build();


        //when
        final ResultActions resultActions = mockMvc.perform(post("/sign-up")
                .param("nickname",signUpForm.getNickname())
                .param("email",signUpForm.getEmail())
                .param("password",signUpForm.getPassword()));



        //then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @DisplayName("회원가입폼 입력 실패")
    @Test
    void 비정상적인_회원가입폼을_입력해서_회원가입을_실패한다() throws Exception{
        // given
        final SignUpForm signUpForm = SignUpForm.builder()
                .nickname("테스트계정입니다")
                .password("password")
                .email("틀린이메일")
                .build();

        // when
        final ResultActions resultActions = mockMvc.perform(post("/sign-up")
                .param("nickname",signUpForm.getNickname())
                .param("email",signUpForm.getEmail())
                .param("password",signUpForm.getPassword()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("인증메일 입력 성공")
    @Test
    void 정상적인_인증메일_이메일을_입력한다() throws Exception{
        // given


        // when


        // then
    }

    @DisplayName("인증메일 입력 실패")
    @Test
    void 비정상적인_인증메일_이메일을_입력한다() throws Exception{

        // given


        // when


        // then

    }

    @DisplayName("회원계정 API 주기 성공")
    @Test
    void 정상적인_접근이_계정찾기_API_호출했을때_폼을_준다() throws Exception{
        //given
        final ResultActions resultActions;

        //when
        resultActions = mockMvc.perform(get("/find-account"));

        // then
        resultActions
                .andExpect(view().name("email/find-account"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원계정 API 주기 실패")
    @Test
    void 비정상적인_접근이_계정찾기_API_호출했을때_폼을_주지않는다() throws Exception{
        //given
        final ResultActions resultActions;

        //when
        resultActions = mockMvc.perform(get("/findAccount"));

        // then
        resultActions
                .andExpect(status().isNotFound());
    }
}
