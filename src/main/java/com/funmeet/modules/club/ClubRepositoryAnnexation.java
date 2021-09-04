package com.funmeet.modules.club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepositoryAnnexation {
    Page<Club> findByKeyword(String keyword, Pageable pageable);
}
