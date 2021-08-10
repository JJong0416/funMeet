package com.funmeet.validator;

import com.funmeet.domain.Account;
import com.funmeet.form.NicknameForm;
import com.funmeet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account getNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if (getNickname != null) {
            errors.rejectValue("nickname", "wrong-value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
