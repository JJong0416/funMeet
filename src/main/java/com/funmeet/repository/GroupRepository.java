package com.funmeet.repository;

import com.funmeet.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Long> {

    boolean existsByGroupPath(String groupPath);
}
