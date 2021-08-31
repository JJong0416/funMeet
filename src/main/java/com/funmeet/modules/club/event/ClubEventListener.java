package com.funmeet.modules.club.event;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountPredicates;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.alert.Alert;
import com.funmeet.modules.alert.AlertRepository;
import com.funmeet.modules.alert.AlertType;
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
    private final AlertRepository alertRepository;
    private final EmailService emailService;

    @EventListener
    public void handleClubEvent(ClubCreatedEvent clubCreatedEvent){
        Club club = clubRepository.findByClubPath(clubCreatedEvent.getClub().getClubPath());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByHobbyAndCity(club.getHobby(), club.getCity()));
        accounts.forEach(account -> {
            if (account.isMeetCreatedByWeb()){
                saveClubAlertByWeb(club,account);
            }

            if ( account.isMeetCreatedByEmail()){
                noticeClubAlertByEmail(club,account);
            }
        });
    }

    private void saveClubAlertByWeb(Club club, Account account) {
        Alert alert = new Alert();
        alert.setTitle(club.getTitle());
        alert.setLink("/club/" + club.getEncodedPath());
        alert.setChecked(false);
        alert.setCreatedLocalDateTime(LocalDateTime.now());
        alert.setMessage(club.getShortDescription());
        alert.setAccount(account);
        alert.setAlertType(AlertType.CREATED);

        alertRepository.save(alert);
    }

    private void noticeClubAlertByEmail(Club club, Account account){
        Context context = new Context();
        context.setVariable("link","/club/" + club.getEncodedPath());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message","새로운 모임이 생겼습니다!");
        context.setVariable("linkName",club.getTitle());
        context.setVariable("host",appProperties.getHost());


        String message = templateEngine.process("email/html_email_link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .subject("뻔(Fun)모임, '" + club.getTitle() + "' 스터디가 생겼습니다.")
                .to(account.getEmail())
                .text(message)
                .build();

        emailService.send(emailMessageForm);

    }

}
