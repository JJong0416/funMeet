package com.funmeet.service;

import com.funmeet.domain.Account;
import com.funmeet.domain.Group;
import com.funmeet.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public Group createGroup(Group group, Account account){
        Group newGroup = groupRepository.save(group);
        newGroup.addManager(account);
        return newGroup;
    }
}
