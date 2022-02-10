package com.funmeet.modules.mapper;

import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.club.form.ClubForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;

@Mapper
public interface ClubMapper {

    ClubMapper INSTANCE = Mappers.getMapper(ClubMapper.class);

    default Club clubFormToEntity(ClubForm clubForm) {
        return Club.builder()
                .managers(new HashSet<>())
                .members(new HashSet<>())
                .hobby(new HashSet<>())
                .city(new HashSet<>())
                .clubPath(clubForm.getClubPath())
                .title(clubForm.getTitle())
                .shortDescription(clubForm.getShortDescription())
                .fullDescription(clubForm.getFullDescription())
                .build();
    }

    ClubDescriptionForm ClubToDescriptionForm(Club club);
}
