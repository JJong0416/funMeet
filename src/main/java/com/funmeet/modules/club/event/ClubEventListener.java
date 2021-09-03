package com.funmeet.modules.club.event;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountPredicates;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.alarm.Alarm;
import com.funmeet.modules.alarm.AlarmRepository;
import com.funmeet.modules.alarm.AlarmType;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@Slf4j
@Async
@RequiredArgsConstructor
public class ClubEventListener {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final AlarmRepository alarmRepository;
    private final EmailService emailService;

    @EventListener
    public void handleClubEvent(ClubCreatedEvent clubCreatedEvent){
        Club club = clubRepository.findByClubPath(clubCreatedEvent.getClub().getClubPath());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByHobbyAndCity(club.getHobby(), club.getCity()));
        accounts.forEach(account -> {
            if (account.isMeetCreatedByWeb()){
                saveClubAlarmByWeb(club,account,club.getShortDescription(),AlarmType.CREATED);
            }

            if ( account.isMeetCreatedByEmail()){
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
                        "뻔모임'" + club.getTitle() + "' 에 새 소식이 있습니다.");
            }
        });
    }


    private void saveClubAlarmByWeb(Club club, Account account,String message, AlarmType alarmType) {
        Alarm alarm = new Alarm();
        alarm.setTitle(club.getTitle());
        alarm.setLink("/club/" + club.getEncodedPath());
        alarm.setChecked(false);
        alarm.setCreatedDateTime(LocalDateTime.now());
        alarm.setMessage(message);
        alarm.setAccount(account);
        alarm.setAlarmType(alarmType);

        alarmRepository.save(alarm);
    }

    private void noticeClubAlarmByEmail(Club club, Account account, String contextMessage, String emailSubject){
        Context context = new Context();
        context.setVariable("link","/club/" + club.getEncodedPath());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message",contextMessage);
        context.setVariable("linkName",club.getTitle());
        context.setVariable("host",appProperties.getHost());


        String message = templateEngine.process("email/html_email_link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .text(message)
                .build();

        emailService.send(emailMessageForm);

    }

}
