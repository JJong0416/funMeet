package com.funmeet.modules.meeting.event;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.ForwardNoticeService;
import com.funmeet.infra.mail.FunMeetSendStrategy;
import com.funmeet.infra.mail.form.EmailMessageForm;
import com.funmeet.infra.mail.SendStrategy;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.alarm.Alarm;
import com.funmeet.modules.alarm.AlarmRepository;
import com.funmeet.modules.alarm.AlarmType;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.enrollment.Enrollment;
import com.funmeet.modules.meeting.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Async
@Component @Transactional
public class EnrollmentEventListener {
    private final AlarmRepository alarmRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final SendStrategy sendStrategy;
    private final ForwardNoticeService forwardNoticeService;

    @EventListener
    public void handleEnrollmentMeeting(EnrollmentEvent enrollmentEvent){
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Meeting meeting = enrollment.getMeeting();
        Club club = meeting.getClub();

        if (account.isMeetEnrollmentResultByEmail()){
            sendEmail(enrollmentEvent,meeting,club,account);
        }

        if (account.isMeetEnrollmentResultByWeb()){
            sendAlarm(enrollmentEvent,meeting,club,account);
        }
    }

    // TODO: 2022-01-04 추상화하기
    private void sendEmail(EnrollmentEvent enrollmentEvent, Meeting meeting, Club club, Account account){
        Context context = new Context();
        context.setVariable("link","/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("linkName",club.getTitle());
        context.setVariable("message",enrollmentEvent.getMessage());
        context.setVariable("host",appProperties.getHost());


        String message = templateEngine.process("email/html-email-link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .subject("뻔모임 '" + meeting.getTitle() + " 미팅 참가 신청 결과입니다.")
                .to(account.getEmail())
                .text(message)
                .build();

//        forwardNoticeService.sendNoticeWithStrategy(new FunMeetSendStrategy(), emailMessageForm);
        sendStrategy.sendNotice(emailMessageForm);
    }

    private void sendAlarm(EnrollmentEvent enrollmentEvent, Meeting meeting, Club club, Account account){

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
