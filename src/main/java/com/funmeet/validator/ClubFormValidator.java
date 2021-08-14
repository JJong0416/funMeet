package com.funmeet.validator;


import com.funmeet.form.ClubForm;
import com.funmeet.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ClubFormValidator implements Validator {

    private final ClubRepository clubRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return ClubForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ClubForm groupForm = (ClubForm)target;

        if (clubRepository.existsByClubPath(groupForm.getClubPath())){
            errors.rejectValue("path", "wrong.path", "해당 경로는 사용할 수 없습니다.");
        }
    }
}
