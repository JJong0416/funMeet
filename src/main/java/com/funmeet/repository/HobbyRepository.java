package com.funmeet.repository;

import com.funmeet.domain.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby,Long> {


    Optional<Hobby> findByTitle(String title);

//    Hobby findByTitle(String title);
}
