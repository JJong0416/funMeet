package com.funmeet.repository;

import com.funmeet.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
