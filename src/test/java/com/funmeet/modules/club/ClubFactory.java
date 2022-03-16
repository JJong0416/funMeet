package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.club.form.ClubForm;

import java.util.HashSet;

public class ClubFactory {

    public static final String CORRECT_CLUB_TITLE = "테스트타이틀1";
    public static final String CORRECT_CLUB_PATH = "test-url001";
    public static final String CORRECT_CLUB_SHORT_DESCRIPTION = "짧은 자기소개입니다.";
    public static final String CORRECT_CLUB_FULL_DESCRIPTION = "긴 자기소개입니다.";

    public static final String CORRECT_UPDATE_CLUB_SHORT_DESCRIPTION = "수정된 짧은 소개입니다.";
    public static final String CORRECT_UPDATE_CLUB_FULL_DESCRIPTION = "수정된 긴 소개입니다.";


    public static Club createRequestClub(final String title, final String clubPath,
                                         final String shortDescription, final String fullDescription) {
        return Club.builder()
                .title(title)
                .clubPath(clubPath)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .build();
    }

    public static Club createCorrectClub() {
        return Club.builder()
                .title(CORRECT_CLUB_TITLE)
                .clubPath(CORRECT_CLUB_PATH)
                .shortDescription(CORRECT_CLUB_SHORT_DESCRIPTION)
                .fullDescription(CORRECT_CLUB_FULL_DESCRIPTION)
                .build();
    }

    public static Club createMakeManagerClub(Account adminAccount) {
        Club builderClub = Club.builder()
                .title(CORRECT_CLUB_TITLE)
                .clubPath(CORRECT_CLUB_PATH)
                .managers(new HashSet<>())
                .members(new HashSet<>())
                .hobby(new HashSet<>())
                .city(new HashSet<>())
                .shortDescription(CORRECT_CLUB_SHORT_DESCRIPTION)
                .fullDescription(CORRECT_CLUB_FULL_DESCRIPTION)
                .build();
        builderClub.addManager(adminAccount);
        return builderClub;
    }

    public static ClubForm createCorrectClubForm() {
        ClubForm clubForm = new ClubForm();
        clubForm.setTitle(CORRECT_CLUB_TITLE);
        clubForm.setClubPath(CORRECT_CLUB_TITLE);
        clubForm.setShortDescription(CORRECT_CLUB_SHORT_DESCRIPTION);
        clubForm.setFullDescription(CORRECT_CLUB_FULL_DESCRIPTION);
        return clubForm;
    }

    public static ClubDescriptionForm createCorrectClubDescriptionForm(){
        ClubDescriptionForm clubDescriptionForm = new ClubDescriptionForm();
        clubDescriptionForm.setShortDescription(CORRECT_UPDATE_CLUB_SHORT_DESCRIPTION);
        clubDescriptionForm.setFullDescription(CORRECT_UPDATE_CLUB_FULL_DESCRIPTION);

        return clubDescriptionForm;
    }
}
