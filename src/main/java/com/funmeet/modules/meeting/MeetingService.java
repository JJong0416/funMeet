package com.funmeet.modules.meeting;


import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.event.ClubUpdateEvent;
import com.funmeet.modules.mapper.MeetingMapper;
import com.funmeet.modules.meeting.event.EnrollmentEventAccepted;
import com.funmeet.modules.meeting.event.EnrollmentEventRejected;
import com.funmeet.modules.meeting.form.MeetingForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;

    public Meeting createMeeting(Account account, Club club , Meeting meeting){

        meeting.setCreatedDateTime(LocalDateTime.now());
        meeting.setCreatedAccount(account);
        meeting.setClub(club);
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임을 만들었습니다."));
        return meetingRepository.save(meeting);
    }

    public void updateMeeting(Meeting meeting, MeetingForm meetingForm) {
        meetingForm.setMeetingType(meeting.getMeetingType());
        MeetingMapper.INSTANCE.updateToExistingEntity(meeting, meetingForm);
        meeting.acceptWaitingList();
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임 정보가 변경되었습니다.."));
    }

    public void deleteMeeting(Meeting meeting) {
        meetingRepository.delete(meeting);
        meeting.acceptWaitingList();
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임 정보가 취소되었습니다.."));
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
        applicationEventPublisher.publishEvent(new EnrollmentEventAccepted(enrollment));
    }

    public void rejectEnrollment(Meeting meeting, Enrollment enrollment) {
        meeting.reject(enrollment);
        applicationEventPublisher.publishEvent(new EnrollmentEventRejected(enrollment));

    }
}
