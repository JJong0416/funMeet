package com.funmeet.modules.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long> {

    boolean existsByClubPath(String path);

    Club findByClubPath(String path);

    boolean existsByTitle(String newTitle);

    Club findClubOnlyByClubPath(String path);
}
