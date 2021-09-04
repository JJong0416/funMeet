package com.funmeet.modules.club;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepositoryAnnexation {
    List<Club> findByKeyword(String keyword);
}
