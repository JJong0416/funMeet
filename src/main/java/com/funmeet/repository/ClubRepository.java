package com.funmeet.repository;

import com.funmeet.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club,Long> {

    boolean existsByClubPath(String path);

    Club findByClubPath(String path);

}
