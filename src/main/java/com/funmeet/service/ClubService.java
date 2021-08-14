package com.funmeet.service;

import com.funmeet.domain.Account;

import com.funmeet.domain.Club;
import com.funmeet.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    public Club createGroup(Club group, Account account){
        Club newGroup = clubRepository.save(group);
        newGroup.addManager(account);
        return newGroup;
    }
}
