package com.funmeet.modules.alert;

import com.funmeet.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    long countByAccountAndChecked(Account account, boolean check);
}
