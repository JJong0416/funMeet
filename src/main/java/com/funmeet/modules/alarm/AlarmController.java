package com.funmeet.modules.alarm;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarm")
    public String getAlarm(@CurrentAccount Account account, Model model) {

        List<Alarm> alarmList = alarmService.findByAccountAndChecked(account, false);
        long numberOfChecked = alarmService.CountCheckedAndNotChecked(account, true);

        showDividedAlarm(model, alarmList, numberOfChecked, alarmList.size());
        model.addAttribute("isNew", true);
        alarmService.markAsRead(alarmList);
        return "alarm/list";
    }

    @GetMapping("/alarm/old")
    public String getOldAlarm(@CurrentAccount Account account, Model model) {
        List<Alarm> alarmList = alarmService.findByAccountAndChecked(account, true);
        long numberOfNotChecked = alarmService.CountCheckedAndNotChecked(account, false);

        showDividedAlarm(model, alarmList, alarmList.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "alarm/list";
    }

    @PostMapping("/alarm/delete")
    public String deleteAlarm(@CurrentAccount Account account) {
        alarmService.deleteAlarm(account, true);
        return "redirect:/alarm";
    }


    private void showDividedAlarm(Model model, List<Alarm> alarmList, long numberOfRead, long numberOfNotRead) {
        List<Alarm> newClubAlarm = new ArrayList<>();
        List<Alarm> alreadyJoinClubAlarm = new ArrayList<>();
        List<Alarm> meetingEnrollmentAlarm = new ArrayList<>();

        for (var alarm : alarmList) {
            switch (alarm.getAlarmType()) {
                case CREATED:
                    newClubAlarm.add(alarm);
                case UPDATED:
                    alreadyJoinClubAlarm.add(alarm);
                case ENROLLMENT:
                    meetingEnrollmentAlarm.add(alarm);
            }
        }

        model.addAttribute("numberOfRead", numberOfRead);
        model.addAttribute("numberOfNotRead", numberOfNotRead);
        model.addAttribute("alarmList", alarmList);
        model.addAttribute("newClubAlarm", newClubAlarm);
        model.addAttribute("alreadyJoinClubAlarm", alreadyJoinClubAlarm);
        model.addAttribute("meetingEnrollmentAlarm", meetingEnrollmentAlarm);
    }
}
