package com.funmeet.modules.alarm;

import com.funmeet.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public void markAsRead(List<Alarm> alarmList){
        alarmList.forEach(n -> n.updateCheck(true));
        alarmRepository.saveAll(alarmList);
    }

    public void deleteAlarm(Account account, boolean check){
        alarmRepository.deleteByAccountAndChecked(account, true);
    }

    public List<Alarm> findByAccountAndChecked(Account account, boolean check){
        return alarmRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, check);
    }

    public Long CountCheckedAndNotChecked(Account account, boolean check){
        return alarmRepository.countByAccountAndChecked(account, check);
    }
}
