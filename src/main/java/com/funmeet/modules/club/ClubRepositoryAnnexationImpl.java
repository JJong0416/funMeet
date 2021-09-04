package com.funmeet.modules.club;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ClubRepositoryAnnexationImpl extends QuerydslRepositorySupport implements ClubRepositoryAnnexation {

    public ClubRepositoryAnnexationImpl(){
        super(Club.class);
    }

    @Override
    public List<Club> findByKeyword(String keyword) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.hobby.any().title.containsIgnoreCase(keyword))
                .or(club.city.any().enCity.containsIgnoreCase(keyword))
                .or(club.city.any().krCity.containsIgnoreCase(keyword)));

        return query.fetch();

    }
}
