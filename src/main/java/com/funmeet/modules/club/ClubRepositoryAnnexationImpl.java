package com.funmeet.modules.club;

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


    // TODO N+1 Select Solve Problem
    @Override
    public Page<Club> findByKeyword(String keyword, Pageable pageable) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.hobby.any().title.containsIgnoreCase(keyword))
                .or(club.city.any().krCity.containsIgnoreCase(keyword)));


        JPQLQuery<Club> pageableQuery = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);
        QueryResults<Club> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());

    }
}
