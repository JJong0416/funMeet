package com.funmeet.validator;

import com.funmeet.form.GroupForm;
import com.funmeet.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class GroupFormValidator implements Validator {

    private final GroupRepository groupRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GroupForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GroupForm groupForm = (GroupForm) target;

        if (groupRepository.existsByGroupPath(groupForm.getGroupPath())){
            errors.rejectValue("path", "wrong.path", "해당 경로는 사용할 수 없습니다.");
        }
    }
}
