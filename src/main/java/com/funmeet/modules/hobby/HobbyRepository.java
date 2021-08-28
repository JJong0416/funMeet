package com.funmeet.modules.hobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby,Long> {
    Optional<Hobby> findByTitle(String title);
}
