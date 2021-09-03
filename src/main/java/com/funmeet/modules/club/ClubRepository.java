package com.funmeet.modules.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long> {

    boolean existsByClubPath(String path);

    Club findByClubPath(String path);

    boolean existsByTitle(String newTitle);

    Club findClubOnlyByClubPath(String path);

    // @EntityGraph(value = "Club.withHobbyAndCity", type = EntityGraph.EntityGraphType.FETCH)
    // TODO EntityGraph 공부. 가지고 오는 DB데이터들이 너무 많음.
    Club findClubWithHobbyAndCityById(Long id);

    Club findClubWithManagersAndMembersById(Long id);
}
