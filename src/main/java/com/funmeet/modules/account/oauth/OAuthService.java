package com.funmeet.modules.account.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Stack;

@Service
@RequiredArgsConstructor
public class OAuthService {

    @Value("${spring.authURL.redirect_uri}")
    private String redirect_uri;

    @Value("${spring.authURL.client_id}")
    private String client_id;

    private final AccountRepository accountRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // 생산 비용 절감, objectMapper는 생산 비용이 상당히 비쌈
    private final RestTemplate rt = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    public OAuthToken getAccessToken(String code) {
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id",client_id);
        params.add("redirect_uri",redirect_uri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );


        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoProfile getProfile(OAuthToken oAuthToken) {
        headers.add("Authorization", "Bearer "+ oAuthToken.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoProfile;
    }

    public Account findAccountByKakaoEmail(String kakaoEmail){
        return accountRepository.findByKakaoEmailAndKakaoTokenVerifiedTrue(kakaoEmail)
                .orElseThrow(() -> { throw new UsernameNotFoundException(kakaoEmail);});

    }
}
