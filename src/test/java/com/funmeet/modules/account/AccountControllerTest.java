//package com.funmeet.modules.account;
//
//import com.funmeet.infra.MockMvcTest;
//import com.funmeet.infra.mail.form.EmailMessageForm;
//import com.funmeet.infra.mail.SendStrategy;
//import com.funmeet.modules.account.form.SignUpForm;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.then;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@ExtendWith(MockitoExtension.class)
//@MockMvcTest
//public class AccountControllerTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired AccountRepository accountRepository;
//    @MockBean
//    SendStrategy sendStrategy;
//
//    @DisplayName("회원가입폼 이동 성공")
//    @Test
//    void 정상적인_접근이_회원가입_API_호출했을때_회원가입폼을_준다() throws Exception {
//
//        // given
//        final ResultActions resultActions;
//
//        // when
//        resultActions = mockMvc.perform(get("/sign-up"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(model().attributeExists("signUpForm"))
//                .andExpect(unauthenticated());
//
//        // then
//        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
//        assertThat(mvcResult).isNotNull();
//    }
//
//    @DisplayName("회원가입폼 입력 성공")
//    @Test
//    void 정상적인_회원가입폼을_입력해서_회원가입을_성공한다() throws Exception{
//        //given
//        final SignUpForm signUpForm = SignUpForm.builder() // 폼에 올바른 값을 입력하고
//                .nickname("account001")
//                .password("password")
//                .email("test@test.com")
//                .build();
//
//        //when
//        final ResultActions resultActions = mockMvc.perform(post("/sign-up")
//                .param("nickname",signUpForm.getNickname())
//                .param("email",signUpForm.getEmail())
//                .param("password",signUpForm.getPassword())
//                .with(csrf()));
//
//        //then
//        resultActions
//                .andExpect(status().is3xxRedirection())
//                .andExpect(authenticated().withUsername(signUpForm.getNickname()))
//                .andExpect(view().name("redirect:/"));
//
//        final Account account = accountRepository.findByEmail(signUpForm.getEmail()).orElseThrow();
//
//        assertNotNull(account);
//        assertNotEquals(account.getPassword(),"wrongPassword");
//        assertTrue(accountRepository.existsByEmail(account.getEmail()));
//        assertTrue(accountRepository.existsByNickname(account.getNickname()));
//        then(sendStrategy).should().send(any(EmailMessageForm.class));
//    }
//
//    @DisplayName("회원가입폼 입력 실패")
//    @Test
//    void 비정상적인_회원가입폼을_입력해서_회원가입을_실패한다() throws Exception{
//        // given
//        final SignUpForm signUpForm = SignUpForm.builder()
//                .nickname("account001")
//                .password("password")
//                .email("틀린이메일")
//                .build();
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(post("/sign-up")
//                .param("nickname",signUpForm.getNickname())
//                .param("email",signUpForm.getEmail())
//                .param("password",signUpForm.getPassword())
//                .with(csrf())); // 기본적으로 Thymeleaf를 사용하면 CSRF 토큰을 넘겨주기 때문에, 안넣으면 403 Forbidden Error
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(unauthenticated())
//                .andExpect(view().name("account/sign-up"));
//    }
//
//    @DisplayName("인증메일 입력 성공")
//    @Test
//    void 정상적인_인증메일_이메일을_입력한다() throws Exception{
//        // given
//        final Account account = accountRepository.save(AccountFactory.createSuccessAccount());
//        account.generateEmailCheckToken();
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(get("/check-email-token")
//                .param("token",account.getEmailCheckToken())
//                .param("email",account.getEmail()));
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(model().attributeDoesNotExist("error"))
//                .andExpect(model().attributeExists("nickname"))
//                .andExpect(view().name("email/check-email"))
//                .andExpect(authenticated());
//        assertNotNull(account);
//    }
//
//    @DisplayName("인증메일 입력 실패")
//    @Test
//    void 비정상적인_인증메일_이메일을_입력한다() throws Exception{
//        // given
//        final Account account = accountRepository.save(AccountFactory.createSuccessAccount());
//        account.generateEmailCheckToken();
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(get("/check-email-token")
//                .param("token","랜덤의_값")
//                .param("email",account.getEmail()));
//
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("error"))
//                .andExpect(view().name("email/check-email"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원계정 API 주기 성공")
//    @Test
//    void 정상적인_접근이_계정찾기_API_호출했을때_폼을_준다() throws Exception{
//        //given
//        final ResultActions resultActions;
//
//        //when
//        resultActions = mockMvc.perform(get("/find-account"));
//
//        // then
//        resultActions
//                .andExpect(view().name("email/find-account"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원계정 API 주기 실패")
//    @Test
//    void 비정상적인_접근이_계정찾기_API_호출했을때_폼을_주지않는다() throws Exception{
//        //given
//        final ResultActions resultActions;
//
//        //when
//        resultActions = mockMvc.perform(get("/findAccount"));
//
//        // then
//        resultActions
//                .andExpect(status().is3xxRedirection()); // Handler 처리
//    }
//}
