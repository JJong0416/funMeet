package com.funmeet.modules.meeting;

import com.funmeet.modules.club.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByClubOrderByStartDateTime(Club club);
}
