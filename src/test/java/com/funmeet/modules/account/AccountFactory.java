package com.funmeet.modules.account;

import com.funmeet.modules.account.form.SignUpForm;

public class AccountFactory {

    static final String CORRECT_ACCOUNT_NICKNAME = "account001";
    static final String CORRECT_ACCOUNT_EMAIL = "test@test.com";
    static final String CORRECT_ACCOUNT_PASSWORD = "password001";
    static final String CORRECT_ACCOUNT_SHORT_BIO = "간략한 자기소개를 추가하세요";

    static final String CORRECT_GUEST_NICKNAME = "account002";
    static final String CORRECT_GUEST_EMAIL = "guest@guest.com";

    static final String WRONG_ACCOUNT_NICKNAME = "test";

    public static SignUpForm createSignupForm() {
        return SignUpForm.builder()
                .nickname(CORRECT_ACCOUNT_NICKNAME)
                .email(CORRECT_ACCOUNT_EMAIL)
                .password(CORRECT_ACCOUNT_PASSWORD)
                .shortBio(CORRECT_ACCOUNT_SHORT_BIO)
                .build();
    }

    public static Account createSuccessAccount() {
        return Account.builder()
                .nickname(CORRECT_ACCOUNT_NICKNAME)
                .email(CORRECT_ACCOUNT_EMAIL)
                .password(CORRECT_ACCOUNT_PASSWORD)
                .shortBio(CORRECT_ACCOUNT_SHORT_BIO)
                .build();
    }

    public static Account createHaveIdAdminAccount(Long id) {
        return new Account(id,
                CORRECT_ACCOUNT_NICKNAME,
                CORRECT_ACCOUNT_PASSWORD,
                CORRECT_GUEST_EMAIL,
                CORRECT_ACCOUNT_SHORT_BIO);
    }

    public static Account createHaveIdGuestAccount(Long id) {
        return new Account(id,
                CORRECT_ACCOUNT_NICKNAME,
                CORRECT_ACCOUNT_PASSWORD,
                CORRECT_GUEST_EMAIL,
                CORRECT_ACCOUNT_SHORT_BIO);
    }
}