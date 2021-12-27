package com.funmeet.modules.account.validator;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.form.NicknameForm;
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
        /* 기존 계정 유무 체크 */
        if (accountRepository.findByNickname(nicknameForm.getNickname()).isPresent()){
            errors.rejectValue("nickname", "wrong-value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
