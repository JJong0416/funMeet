package com.funmeet.modules.club.event;

import com.funmeet.modules.club.Club;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClubUpdateEvent {

    private final Club club;

    private final String message;
}
