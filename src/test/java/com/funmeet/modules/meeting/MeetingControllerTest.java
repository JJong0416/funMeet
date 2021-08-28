package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountFactory;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.AccountService;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MeetingControllerTest{

    @Autowired private MeetingService meetingService;
    @Autowired private EnrollmentRepository enrollmentRepository;
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
    @DisplayName("선착순 참가 신청 - 수락")
    void Enrollment_FCFS_meeting_right() throws Exception {
        Account adminAccount = accountFactory.createNewAccount("adminAccount");
        Club club = clubFactory.createNewClub("test-path",adminAccount);
        Meeting meeting = createNewMeeting("테스트미팅", MeetingType.FCFSB,2,1000,club,adminAccount);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/meeting/" + meeting.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/meeting/" + meeting.getId()));

        Account joinAccount = accountRepository.findByNickname("jongchan");

        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());

    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 인원 full, 선착순 대기중")
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void Enrollment_FCFS_meeting_fail() throws Exception {
        Account adminAccount = accountFactory.createNewAccount("adminAccount");
        Club club = clubFactory.createNewClub("test-path",adminAccount);
        Meeting meeting = createNewMeeting("테스트미팅",MeetingType.FCFSB,2,1000,club,adminAccount);

        Account lastAccount = accountRepository.findByNickname("jongchan");
        Account joinAccount1 = accountFactory.createNewAccount("check1");
        Account joinAccount2 = accountFactory.createNewAccount("check2");
        meetingService.newEnrollment(meeting,joinAccount1);
        meetingService.newEnrollment(meeting,joinAccount2);
        meetingService.newEnrollment(meeting,lastAccount);



        mockMvc.perform(post("/club/" + club.getClubPath() + "/meeting/" + meeting.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/meeting/" + meeting.getId()));

        /*  먼저 인원수까지 채워넣기*/
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount1).isAccepted());
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount2).isAccepted());

        /* limity 확인 */
        assertFalse(enrollmentRepository.findByMeetingAndAccount(meeting,lastAccount).isAccepted());
    }


    @Test
    @DisplayName("모임 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void CancelEnrollment_FCFS_meeting_and_next_accepted() throws Exception {
        Account adminAccount = accountRepository.findByNickname("jongchan");
        Account joinAccount = accountFactory.createNewAccount("테스트1");
        Account lastAccount = accountFactory.createNewAccount("테스트2");

        Club club = clubFactory.createNewClub("test-path",adminAccount);
        Meeting meeting = createNewMeeting("test-metting",MeetingType.FCFSB,2,1000,club,adminAccount);

        meetingService.newEnrollment(meeting, adminAccount);
        meetingService.newEnrollment(meeting, joinAccount);
        meetingService.newEnrollment(meeting, lastAccount);

        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,adminAccount).isAccepted());
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());

        assertFalse(enrollmentRepository.findByMeetingAndAccount(meeting,lastAccount).isAccepted()); // 인원 초과 - 대기중

        /* userdetail에 jongchan이므로 jongchan이 meeting에서 나감 */
        mockMvc.perform(post("/club/" + club.getClubPath() + "/meeting/" + meeting.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/meeting/" + meeting.getId()));


        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,lastAccount).isAccepted());
        assertNull(enrollmentRepository.findByMeetingAndAccount(meeting, adminAccount));
    }

    @Test
    @DisplayName("비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void CancelEnrollment_FCFS_meeting_and_next_accepted_and_restAccount_remains() throws Exception {
        Account adminAccount = accountRepository.findByNickname("jongchan");
        Account joinAccount = accountFactory.createNewAccount("테스트1");
        Account lastAccount = accountFactory.createNewAccount("테스트2");

        Club club = clubFactory.createNewClub("test-path",adminAccount);
        Meeting meeting = createNewMeeting("test-metting",MeetingType.FCFSB,2,1000,club,adminAccount);

        meetingService.newEnrollment(meeting, joinAccount);
        meetingService.newEnrollment(meeting, lastAccount);
        meetingService.newEnrollment(meeting, adminAccount);

        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());

        assertFalse(enrollmentRepository.findByMeetingAndAccount(meeting,adminAccount).isAccepted()); // 인원 초과 - 대기중

        /* userdetail에 jongchan이므로 jongchan이 meeting에서 나감 */
        mockMvc.perform(post("/club/" + club.getClubPath() + "/meeting/" + meeting.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/meeting/" + meeting.getId()));

        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,joinAccount).isAccepted());
        assertTrue(enrollmentRepository.findByMeetingAndAccount(meeting,lastAccount).isAccepted());
        assertNull(enrollmentRepository.findByMeetingAndAccount(meeting,adminAccount));
    }


    @Test
    @DisplayName("매너지를 통한 모임 신청 - 대기중")
    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void Enrollment_CONFRIM_meeting_right_Waiting() throws Exception {
        Account adminAccount = accountRepository.findByNickname("jongchan");
        Account joinAccount = accountFactory.createNewAccount("테스트1");
        Account lastAccount = accountFactory.createNewAccount("테스트2");

        Club club = clubFactory.createNewClub("test-path",adminAccount);
        Meeting meeting = createNewMeeting("test-metting",MeetingType.CONFIRM,2,1000,club,adminAccount);

        meetingService.newEnrollment(meeting, adminAccount);
        meetingService.newEnrollment(meeting, joinAccount);
        meetingService.newEnrollment(meeting, lastAccount);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/meeting/" + meeting.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/meeting/" + meeting.getId()));

        assertFalse(enrollmentRepository.findByMeetingAndAccount(meeting,lastAccount).isAccepted());
    }

    private Meeting createNewMeeting(String meetingTitle, MeetingType meetingType, int limitPerson, int meetingPrice ,Club club, Account account) {
        Meeting meeting = new Meeting();
        meeting.setTitle(meetingTitle);
        meeting.setMeetingType(meetingType);
        meeting.setLimitOfEnrollments(limitPerson);
        meeting.setMeetingPrice(1000);
        meeting.setCreatedDateTime(LocalDateTime.now());
        meeting.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
        meeting.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(2));
        return meetingService.createMeeting(account, club, meeting);
    }

}