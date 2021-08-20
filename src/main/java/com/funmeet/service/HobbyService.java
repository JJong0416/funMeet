package com.funmeet.service;

import com.funmeet.domain.Hobby;
import com.funmeet.repository.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;

    public Hobby findOrCreateHobby(String hobbyTitle){
        Hobby hobby = hobbyRepository.findByTitle(hobbyTitle).orElseGet( () -> hobbyRepository.save(Hobby.builder()
                .title(hobbyTitle)
                .build()));

        return hobby;
    }
}
