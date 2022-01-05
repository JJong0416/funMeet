package com.funmeet.modules.meeting;


import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubService;
import com.funmeet.modules.club.event.ClubUpdateEvent;
import com.funmeet.modules.enrollment.Enrollment;
import com.funmeet.modules.enrollment.EnrollmentRepository;
import com.funmeet.modules.mapper.MeetingMapper;
import com.funmeet.modules.meeting.event.EnrollmentEventAccepted;
import com.funmeet.modules.meeting.event.EnrollmentEventRejected;
import com.funmeet.modules.meeting.form.MeetingForm;
import com.funmeet.modules.meeting.validator.MeetingFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MeetingFormValidator meetingFormValidator;

    public Meeting createMeeting(Account account, Club club , MeetingForm meetingForm){
        Meeting meeting = MeetingMapper.INSTANCE.MeetingFormToEntity(meetingForm); // Mapping
        meeting.createMeeting(account, club);

        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임을 만들었습니다."));

        return meetingRepository.save(meeting);
    }

    public void updateMeeting(Meeting meeting, MeetingForm meetingForm) {
        meetingForm.setMeetingType(meeting.getMeetingType());
        meeting.updateMeeting(meetingForm);
        meeting.acceptWaitingList();
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임 정보가 변경되었습니다.."));
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = this.getMeetingById(id);
        meeting.acceptWaitingList();
        meetingRepository.delete(meeting);

        applicationEventPublisher.publishEvent(new ClubUpdateEvent(meeting.getClub(),
                "'" + meeting.getTitle() + "' 모임 정보가 취소되었습니다.."));
    }

    public void newEnrollment(Account account, Long id){

        Meeting meeting = meetingRepository.findById(id).orElseThrow();

        if (!enrollmentRepository.existsByMeetingAndAccount(meeting,account)){
            Enrollment enrollment = Enrollment.builder()
                    .enrolledAt(LocalDateTime.now())
                    .meeting(meeting)
                    .account(account)
                    .accepted(meeting.isAbleToWaitingEnrollment())
                    .shortIntroduce("안녕하세요!")
                    .build();
            meeting.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Account account, Long id){
        Meeting meeting = this.getMeetingById(id);
        Enrollment enrollment = enrollmentRepository.findByMeetingAndAccount(meeting,account);

        meeting.removeEnrollment(enrollment); // 위임
        enrollmentRepository.delete(enrollment); // 삭제
        meeting.acceptNextWaitingEnrollment(); // 다음 대기자
    }

    public Meeting acceptEnrollment(Long meetingId, Long enrollmentId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        meeting.accept(enrollment);
        applicationEventPublisher.publishEvent(new EnrollmentEventAccepted(enrollment));

        return meeting;
    }

    public Meeting rejectEnrollment(Long meetingId, Long enrollmentId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        meeting.reject(enrollment);
        applicationEventPublisher.publishEvent(new EnrollmentEventRejected(enrollment));

        return meeting;
    }

    public List<Meeting> getNewMeeting(Club club){
        List<Meeting> meetings = meetingRepository.findByClubOrderByStartDateTime(club);
        List<Meeting> newMeetings = new ArrayList<>();

        meetings.forEach(e -> {
            if (!e.getEndDateTime().isBefore(LocalDateTime.now())) {
                newMeetings.add(e);
            }});

        return newMeetings;
    }

    public List<Meeting> getOldMeeting(Club club){
        List<Meeting> meetings = meetingRepository.findByClubOrderByStartDateTime(club);
        List<Meeting> oldMeetings = new ArrayList<>();

        meetings.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldMeetings.add(e);
            }});

        return oldMeetings;
    }

    public Meeting getMeetingById(Long id){
        return meetingRepository.findById(id).orElseThrow();
    }

    public MeetingForm meetingToMeetingForm(Meeting meeting){
        return MeetingMapper.INSTANCE.MeetingToMeetingForm(meeting);
    }

    public Meeting getMeetingWithValidation(Long id, MeetingForm meetingForm, Errors errors) {
        Meeting meeting = this.getMeetingById(id);
        meetingFormValidator.validateUpdateForm(meetingForm, meeting, errors);
        return meeting;
    }
}
