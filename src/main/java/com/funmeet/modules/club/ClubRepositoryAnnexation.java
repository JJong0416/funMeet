package com.funmeet.modules.club;

import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Transactional(readOnly = true)
public interface ClubRepositoryAnnexation {

    Page<Club> findByKeyword(String keyword, Pageable pageable);

    List<Club> findClubByAccount(Set<Hobby> hobbies, Set<City> cities);
}
