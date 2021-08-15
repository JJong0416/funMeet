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
        ClubForm clubForm = (ClubForm)target;
        if (clubRepository.existsByClubPath(clubForm.getClubPath())) {
            errors.rejectValue("path", "wrong.path", "해당 모임의 경로값을 사용할 수 없습니다.");
        }
    }
}
