package com.funmeet.modules.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    /* 설계: 안읽은 메일을 확인했을 경우, 모든 알림들을 별도의 로직으로 빼는 것이 아닌, 단번에 일괄 읽음 처리. */
    public void markAsRead(List<Alarm> alarmList){
        alarmList.forEach(n -> n.setChecked(true));
        alarmRepository.saveAll(alarmList);
    }
}
