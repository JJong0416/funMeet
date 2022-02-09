package com.funmeet.modules.account;

import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.mapper.AccountMapper;

public class AccountFactory {

    static final String CORRECT_ACCOUNT_NICKNAME = "account001";
    static final  String CORRECT_ACCOUNT_EMAIL = "test@test.com";
    static final String CORRECT_ACCOUNT_PASSWORD = "password001";
    static final String CORRECT_ACCOUNT_SHORT_BIO = "간략한 자기소개를 추가하세요";

    static final String WRONG_ACCOUNT_NICKNAME = "test";

    public static Account createRequestAccount(final String accountNickname, final String accountEmail){
        return Account.builder()
                .nickname(accountNickname)
                .email(accountEmail)
                .password(CORRECT_ACCOUNT_PASSWORD)
                .shortBio(CORRECT_ACCOUNT_SHORT_BIO)
                .build();
    }

    public static Account createSignUpFormAccount(final SignUpForm signUpForm){
        return AccountMapper.INSTANCE.signUpFormToEntity(signUpForm);
    }

    public static Account createSuccessAccount(){
        return Account.builder()
                .nickname(CORRECT_ACCOUNT_NICKNAME)
                .email(CORRECT_ACCOUNT_EMAIL)
                .password(CORRECT_ACCOUNT_PASSWORD)
                .shortBio(CORRECT_ACCOUNT_SHORT_BIO)
                .build();
    }

    public static Account createWrongAccount(){
        return  Account.builder()
                .nickname(WRONG_ACCOUNT_NICKNAME) // 틀린 닉네임
                .email(CORRECT_ACCOUNT_EMAIL)
                .password(CORRECT_ACCOUNT_PASSWORD)
                .shortBio(CORRECT_ACCOUNT_SHORT_BIO)
                .build();
    }
}