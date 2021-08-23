package com.funmeet.service;


import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import com.funmeet.domain.Meeting;
import com.funmeet.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public Meeting createMeeting(Account account, Club club , Meeting meeting){

        meeting.setCreatedDateTime(LocalDateTime.now());
        meeting.setCreatedAccount(account);
        meeting.setClub(club);
        return meetingRepository.save(meeting);
    }
}
