package com.funmeet.modules.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long>, ClubRepositoryAnnexation {

    boolean existsByClubPath(String clubPath);

    Club findByClubPath(String path);

    @EntityGraph(attributePaths = {"hobby","managers"}) // 시간이 더 걸리네..
    Club findClubWithHobbyByClubPath(String path);

//    @EntityGraph(attributePaths = {"city,managers"})
    Club findClubWithCityByClubPath(String path);

    @EntityGraph(attributePaths = "members")
    Club findClubWithMembersByClubPath(String path);

    @EntityGraph(attributePaths = "managers")
    Club findClubWithManagersByClubPath(String path);

    Club findClubOnlyByClubPath(String path);

    Club findClubWithHobbyAndCityById(Long id);

    Club findClubWithMembersAndManagersById(Long id);

    Club findClubWithManagersAndMembersById(Long id);
}


/*
*
*
*/