package com.funmeet.modules.club.event;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.ForwardNoticeService;
import com.funmeet.infra.mail.FunMeetSendStrategy;
import com.funmeet.infra.mail.form.EmailMessageForm;
import com.funmeet.infra.mail.SendStrategy;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountPredicates;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.alarm.Alarm;
import com.funmeet.modules.alarm.AlarmRepository;
import com.funmeet.modules.alarm.AlarmType;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component @Transactional
@Async @RequiredArgsConstructor
public class ClubEventListener {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final AlarmRepository alarmRepository;
    private final ForwardNoticeService forwardNoticeService;

    @EventListener
    public void handleClubCreateEvent(ClubCreatedEvent clubCreatedEvent){
        Club club = clubRepository.findClubWithHobbyAndCityById(clubCreatedEvent.getClub().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByHobbyAndCity(club.getHobby(), club.getCity()));
        accounts.forEach(account -> {
            if (account.isMeetCreatedByWeb()){
                saveClubAlarmByWeb(club,account,club.getShortDescription(),AlarmType.CREATED);
            }

            if (account.isMeetCreatedByEmail()){
                noticeClubAlarmByEmail(club,account,"새로운 모임이 생겼습니다","뻔모임, '" + club.getTitle() + "' 모임이 생겼습니다.");
            }
        });
    }

    @EventListener
    public void handleClubUpdateEvent(ClubUpdateEvent clubUpdateEvent) {
        Club club = clubRepository.findClubWithManagersAndMembersById(clubUpdateEvent.getClub().getId());
        List<Account> accounts = new ArrayList<>();
        accounts.addAll(club.getMembers());
        accounts.addAll(club.getManagers());

        accounts.forEach(account -> {

            if (account.isMeetCreatedByWeb()) {
                saveClubAlarmByWeb(club, account, clubUpdateEvent.getMessage(), AlarmType.UPDATED);
            }

            if (account.isMeetCreatedByEmail()) {
                noticeClubAlarmByEmail(club, account, clubUpdateEvent.getMessage(),
                        "뻔모임'" + club.getTitle() + "' 에 업데이트 소식이 있습니다.");
            }
        });
    }


    private void saveClubAlarmByWeb(Club club, Account account,String message, AlarmType alarmType) {

        Alarm alarm = Alarm.builder()
                .title(club.getTitle())
                .link("/club/" + club.getEncodedPath())
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .message(message)
                .account(account)
                .AlarmType(alarmType)
                .build();

        alarmRepository.save(alarm);
    }

    private void noticeClubAlarmByEmail(Club club, Account account, String contextMessage, String emailSubject){
        Context context = new Context();
        context.setVariable("link","/club/" + club.getEncodedPath());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message",contextMessage);
        context.setVariable("linkName",club.getTitle());
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("email/html-email-link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .text(message)
                .build();

//        forwardNoticeService.sendNoticeWithStrategy(new FunMeetSendStrategy(), emailMessageForm);
    }

}
