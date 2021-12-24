package com.funmeet.modules.hobby;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;

    public Hobby findOrCreateHobby(String hobbyTitle){
        return hobbyRepository.findByTitle(hobbyTitle).orElseGet( () -> hobbyRepository.save(Hobby.builder()
                .title(hobbyTitle)
                .build()));
    }
}
