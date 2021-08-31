package com.funmeet.modules.club.event;

import com.funmeet.modules.club.Club;
import lombok.Getter;


@Getter
public class ClubCreatedEvent {

    private final Club club;

    public ClubCreatedEvent(Club club){
        this.club = club;
    }
}
