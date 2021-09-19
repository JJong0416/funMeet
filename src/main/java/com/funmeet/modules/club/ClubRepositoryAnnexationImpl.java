package com.funmeet.modules.club;

import com.funmeet.modules.account.QAccount;
import com.funmeet.modules.city.QCity;
import com.funmeet.modules.hobby.QHobby;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Objects;

public class ClubRepositoryAnnexationImpl extends QuerydslRepositorySupport implements ClubRepositoryAnnexation {

    public ClubRepositoryAnnexationImpl(){
        super(Club.class);
    }

    @Override
    public Page<Club> findByKeyword(String keyword, Pageable pageable) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.hobby.any().title.containsIgnoreCase(keyword))
                .or(club.city.any().krCity.containsIgnoreCase(keyword)))
                .leftJoin(club.hobby, QHobby.hobby).fetchJoin()
                .leftJoin(club.city, QCity.city).fetchJoin()
                .leftJoin(club.members, QAccount.account).fetchJoin()
                .distinct();

        JPQLQuery<Club> pageableQuery = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);
        QueryResults<Club> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());

    }
}