package com.funmeet.modules.account.oauth;

import lombok.Data;

@Data
public class KakaoProfile {

    /* Field명을 REST API 형식으로 주고 싶은데, OAuth2 형식은 _로 주기
    *  이렇게 작성하는것이 최대이다.
    *  */

    public Integer id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;

    @Data
    public class Properties {
        public String nickname;
    }

    @Data
    public class KakaoAccount {

        public Boolean profile_nickname_needs_agreement;
        public Profile profile;
        public Boolean has_email;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public String email;

        @Data
        public class Profile {
            public String nickname;
        }
    }
}