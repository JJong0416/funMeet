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

    public Club createNewClub(Club club, Account account){
        Club createClub = clubRepository.save(club);
        createClub.addManager(account);
        return createClub;
    }
}
