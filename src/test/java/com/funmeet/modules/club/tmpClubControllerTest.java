//package com.funmeet.modules.club;
//
//import com.funmeet.infra.AbstractContainerBaseTest;
//import com.funmeet.infra.MockMvcTest;
//import com.funmeet.modules.account.Account;
//import com.funmeet.modules.account.AccountRepository;
//import com.funmeet.modules.account.AccountService;
//import com.funmeet.modules.account.form.SignUpForm;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.test.context.support.TestExecutionEvent;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@MockMvcTest
//class ClubControllerTest extends AbstractContainerBaseTest {
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ClubService clubService;
//    @Autowired private ClubRepository clubRepository;
//    @Autowired private AccountRepository accountRepository;
//    @Autowired private AccountService accountService;
//
//    public Account jongchan = null;
//
//    @BeforeEach
//    void beforeEach(){
// UserDetails Account 생성
//
//        SignUpForm signUpForm = new SignUpForm();
//        signUpForm.setNickname("jongchan");
//        signUpForm.setPassword("12345678");
//        signUpForm.setEmail("jjong@email.com");
//        accountService.processSignUpAccount(signUpForm);
//    }
//
//    @AfterEach
//    void afterEach() {
//        accountRepository.deleteAll();
//    }
//
//
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("모임 등록 폼")
//    void createClubForm() throws Exception {
//        mockMvc.perform(get("/create-club"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("club/form"))
//                .andExpect(model().attributeExists("account"))
//                .andExpect(model().attributeExists("clubForm"));
//    }
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("모임 등록 - 완료")
//    void createClub_right() throws Exception {
//        mockMvc.perform(post("/create-club")
//                .param("title", "FunMeet title")
//                .param("clubPath", "url1")
//                .param("shortDescription", "짧게 보기 체크")
//                .param("fullDescription", "상세 보기 체크")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/club/url1"));
//
//        Club club = clubRepository.findByClubPath("url1");
//        assertNotNull(club);
//        Account account = accountRepository.findByNickname("jongchan");
//        assertTrue(club.getManagers().contains(account));
//    }
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("모임 등록 - 실패")
//    void createClub_wrong() throws Exception {
//        mockMvc.perform(post("/create-club")
//                .param("title", "FunMeet title")
//                .param("clubPath", "Wrong Path")
//                .param("shortDescription", "짧게 보기 체크")
//                .param("fullDescription", "상세 보기 체크")
//                .with(csrf()))
//
//                .andExpect(status().isOk())
//                .andExpect(view().name("club/form"))
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeExists("clubForm"))
//                .andExpect(model().attributeExists("account"));
//
//
//        Club club = clubRepository.findByClubPath("Wrong Path");
//        assertNull(club);
//    }
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("모임 조회")
//    void pageClub() throws Exception {
//        Club club = new Club();
//        club.setClubPath("test");
//        club.setTitle("테스트 제목");
//        club.setShortDescription("짧게 보기 체크");
//        club.setFullDescription("<p>길게 보기 체크</p>");
//
//        Account account = accountRepository.findByNickname("jongchan");
//        clubService.createNewClub(club,account);
//
//        mockMvc.perform(get("/club/test"))
//                .andExpect(view().name("club/page"))
//                .andExpect(model().attributeExists("account"))
//                .andExpect(model().attributeExists("club"));
//
//    }
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("모임 가입 가입")
//    void joinNewClub() throws Exception {
//        Account managerAccount = createNewAccount("account");
//        Account jongchan = accountRepository.findByNickname("jongchan");
//
//        Club club = createNewClub("url",managerAccount);
//        clubService.addMember(jongchan,"url");
//
//        mockMvc.perform(get("/club/" + club.getClubPath() + "/join"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/members"));
//
//        assertTrue(club.getMembers().contains(jongchan));
//        assertTrue(club.getManagers().contains(managerAccount));
//    }
//
//    @Test
//    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("스터디 탈퇴")
//    void leaveClub() throws Exception {
//        Account managerAccount = createNewAccount("account");
//        Account jongchan = accountRepository.findByNickname("jongchan");
//
//        Club club = createNewClub("url",managerAccount);
//        clubService.addMember(jongchan,"url");
//
//        mockMvc.perform(get("/club/" + club.getClubPath() + "/leave"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/members"));
//
//        assertFalse(club.getMembers().contains(jongchan));
//    }
//
//    protected Account createNewAccount(String nickname){
//        Account account = new Account();
//        account.setNickname(nickname);
//        account.setEmail( nickname + "@gmail.com");
//        account.setPassword("123456");
//        accountRepository.save(account);
//        return account;
//    }
//
//    protected Club createNewClub(String clubPath, Account Manager){
//        Club club = new Club();
//        club.setClubPath(clubPath);
//        clubService.createNewClub(club,Manager);
//        return club;
//    }
//}