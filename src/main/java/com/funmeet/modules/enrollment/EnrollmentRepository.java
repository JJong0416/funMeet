package com.funmeet.modules.enrollment;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.meeting.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMeetingAndAccount(Meeting meeting, Account account);

    Enrollment findByMeetingAndAccount(Meeting meeting, Account account);
}
