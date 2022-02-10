package com.funmeet.modules.notice;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.FunMeetSendStrategy;
import com.funmeet.infra.mail.StrategyFactory;
import com.funmeet.infra.mail.SendStrategy;
import com.funmeet.infra.mail.StrategyName;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.meeting.Meeting;
import com.funmeet.modules.meeting.event.EnrollmentEvent;
import com.funmeet.modules.notice.form.MessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class NoticeFactory extends NoticeConstant {

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final StrategyFactory strategyFactory;

    public MessageForm makeAccountNoticeForm(Account account, String link, String linkName) {
        final String fullLink = link + account.getEmailCheckToken() + EMAIL + account.getEmail();

        String message = makeTemplateEngineMessage(fullLink, account.getNickname(), CLICK_MESSAGE, linkName);

        return makeMessageForm(LOGIN_LINK_MESSAGE, account.getEmail(), message);
    }

    public void noticeClubAlarmByEmail(Club club, Account account, String contextMessage, String emailSubject) {
        final String fullLink = "/club/" + club.getEncodedPath();

        String message = makeTemplateEngineMessage(fullLink, account.getNickname(), contextMessage, club.getTitle());

        MessageForm messageForm = makeMessageForm(emailSubject, account.getEmail(), message);
        sendEmail(messageForm);
    }

    public void noticeEnrollmentByEmail(EnrollmentEvent enrollmentEvent, Meeting meeting, Club club, Account account) {
        final String fullLink = "/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();

        String message = makeTemplateEngineMessage(fullLink, account.getNickname(), enrollmentEvent.getMessage(), club.getTitle());

        MessageForm messageForm = makeMessageForm(
                "뻔모임 '" + meeting.getTitle() + " 미팅 참가 신청 결과입니다.", account.getEmail(), message);
        sendEmail(messageForm);
    }

    public void sendEmail(MessageForm messageForm){
        SendStrategy sendStrategy = strategyFactory.findStrategy(StrategyName.EMAIL);
        sendStrategy.sendNotice(messageForm);
    }

    private String makeTemplateEngineMessage(String link, String nickName, String message, String linkName) {
        Context context = new Context();
        context.setVariable(LINK, link);
        context.setVariable(NICKNAME, nickName);
        context.setVariable(MESSAGE, message);
        context.setVariable(LINK_NAME, linkName);
        context.setVariable(HOST, appProperties.getHost());
        return templateEngine.process(EMAIL_PROCESS_LINK, context);

    }

    private MessageForm makeMessageForm(String subject, String to, String text) {
        return MessageForm.builder()
                .subject(subject)
                .to(to)
                .text(text)
                .build();
    }
}
