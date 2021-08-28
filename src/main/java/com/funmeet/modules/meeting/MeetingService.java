package com.funmeet.modules.meeting;


import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.meeting.form.MeetingForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final ModelMapper modelMapper;

    private final MeetingRepository meetingRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Meeting createMeeting(Account account, Club club , Meeting meeting){

        meeting.setCreatedDateTime(LocalDateTime.now());
        meeting.setCreatedAccount(account);
        meeting.setClub(club);
        return meetingRepository.save(meeting);
    }

    public void updateMeeting(Meeting meeting, MeetingForm meetingForm) {
        modelMapper.map(meetingForm, meeting);
        meeting.acceptWaitingList();
    }

    public void deleteMeeting(Meeting meeting) {
        meetingRepository.delete(meeting);
        meeting.acceptWaitingList();
    }

    public void newEnrollment(Meeting meeting, Account account){
        if (!enrollmentRepository.existsByMeetingAndAccount(meeting,account)){
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setMeeting(meeting);
            enrollment.setAccount(account);
            enrollment.setAccepted(meeting.isAbleToWaitingEnrollment());
            enrollment.setShortIntroduce("안녕하세요!");

            meeting.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Meeting meeting, Account account){
        Enrollment enrollment = enrollmentRepository.findByMeetingAndAccount(meeting,account);

        meeting.removeEnrollment(enrollment); // 위임
        enrollmentRepository.delete(enrollment); // 삭제
        meeting.acceptNextWaitingEnrollment(); // 다음 대기자
    }

    public void acceptEnrollment(Meeting meeting, Enrollment enrollment) {
        meeting.accept(enrollment);
    }

    public void rejectEnrollment(Meeting meeting, Enrollment enrollment) {
        meeting.reject(enrollment);
    }
}