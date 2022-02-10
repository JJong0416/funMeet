package com.funmeet.modules.meeting.event;

import com.funmeet.infra.mail.ConveyStrategyService;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.alarm.Alarm;
import com.funmeet.modules.alarm.AlarmRepository;
import com.funmeet.modules.alarm.AlarmType;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.enrollment.Enrollment;
import com.funmeet.modules.meeting.Meeting;
import com.funmeet.modules.notice.NoticeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Async
@Component
@Transactional
public class EnrollmentEventListener {
    private final AlarmRepository alarmRepository;
    private final ConveyStrategyService conveyStrategyService;
    private final NoticeFactory noticeFactory;

    @EventListener
    public void handleEnrollmentMeeting(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Meeting meeting = enrollment.getMeeting();
        Club club = meeting.getClub();

        if (account.isMeetEnrollmentResultByEmail()) {
            noticeFactory.noticeEnrollmentByEmail(enrollmentEvent, meeting, club, account);
        }

        if (account.isMeetEnrollmentResultByWeb()) {
            sendAlarm(enrollmentEvent, meeting, club, account);
        }
    }

    private void sendAlarm(EnrollmentEvent enrollmentEvent, Meeting meeting, Club club, Account account) {
        Alarm alarm = Alarm.builder()
                .title(club.getTitle() + "/" + meeting.getTitle())
                .link("/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId())
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .message(enrollmentEvent.getMessage())
                .account(account)
                .AlarmType(AlarmType.ENROLLMENT)
                .build();

        alarmRepository.save(alarm);
    }
}
