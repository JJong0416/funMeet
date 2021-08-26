package com.funmeet.repository;

import com.funmeet.domain.Account;
import com.funmeet.domain.Enrollment;
import com.funmeet.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMeetingAndAccount(Meeting meeting, Account account);

    Enrollment findByMeetingAndAccount(Meeting meeting, Account account);
}
