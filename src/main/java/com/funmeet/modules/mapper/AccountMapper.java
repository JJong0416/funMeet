package com.funmeet.modules.mapper;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.oauth.OAuthForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    Account signUpFormToEntity(SignUpForm signUpForm);

    Account oauthFormToEntity(OAuthForm oAuthForm);

    NotificationForm AccountToNotificationForm(Account account);
}
