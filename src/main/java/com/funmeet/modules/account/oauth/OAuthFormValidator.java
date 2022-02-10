package com.funmeet.modules.account.oauth;

import com.funmeet.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class OAuthFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(OAuthForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OAuthForm oAuthForm = (OAuthForm) target;

        if (accountRepository.existsByNickname(oAuthForm.getNickname())) {
            errors.rejectValue("nickname", "invalid nickname", new Object[]{oAuthForm.getNickname()}, "이미 사용중인 아이디입니다.");
        }
    }
}
