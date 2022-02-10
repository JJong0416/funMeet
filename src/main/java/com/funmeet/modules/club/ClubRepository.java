package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club, Long>, ClubRepositoryAnnexation {

    boolean existsByClubPath(String clubPath);

    Club findByClubPath(String path);

    @EntityGraph(attributePaths = {"hobby", "managers"})
    Club findClubWithHobbyByClubPath(String path);

    @EntityGraph(attributePaths = {"managers", "members"})
    Club findClubWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = "members")
    Club findClubWithMembersByClubPath(String path);

    @EntityGraph(attributePaths = "managers")
    Club findClubWithManagersByClubPath(String path);

    Club findClubOnlyByClubPath(String path);

    @EntityGraph(attributePaths = "managers")
    Club findClubWithHobbyAndCityById(Long id);

    Club findClubWithCityByClubPath(String path);

    @EntityGraph(attributePaths = {"hobby", "city"})
    List<Club> findFirst9ByPublishedAndClosedOrderByPublishDateTimeDesc(boolean published, boolean closed);

    List<Club> findList5ByManagersContainingAndClosedOrderByPublishDateTimeDesc(Account account, boolean closed);

    List<Club> findList5ByMembersContainingAndClosedOrderByPublishDateTimeDesc(Account account, boolean closed);

}
