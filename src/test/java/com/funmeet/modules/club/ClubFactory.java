package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;

public class ClubFactory {

    static final String CORRECT_CLUB_TITLE = "테스트타이틀1";
    static final String CORRECT_CLUB_PATH = "test-url001";
    static final String CORRECT_CLUB_SHORT_DESCRIPTION = "짧은 자기소개입니다.";
    static final String CORRECT_CLUB_FULL_DESCRIPTION = "긴 자기소개입니다.";

    public static Club createRequestClub(final String title, final String clubPath,
                                  final String shortDescription, final String fullDescription){
        return Club.builder()
                .title(title)
                .clubPath(clubPath)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .build();
    }

    public static Club createCorrectClub(){
        return Club.builder()
                .title(CORRECT_CLUB_TITLE)
                .clubPath(CORRECT_CLUB_PATH)
                .shortDescription(CORRECT_CLUB_SHORT_DESCRIPTION)
                .fullDescription(CORRECT_CLUB_FULL_DESCRIPTION)
                .build();
    }
}
