/*
package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClubFactory {

    @Autowired ClubService clubService;
    @Autowired ClubRepository clubRepository;

    public Club createNewClub(String path, Account manager) {
        Club club = new Club();
        club.setClubPath(path);
        clubService.createNewClub(club, manager);
        return club;
    }
}*/
