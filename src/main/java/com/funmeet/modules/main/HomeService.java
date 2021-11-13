package com.funmeet.modules.main;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeService {

    @Value("${spring.authURL.client_id}")
    private String clientId;

    @Value("${spring.authURL.redirect_uri}")
    private String redirectUri;

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final AppProperties appProperties;

    public StringBuilder getOAuthLink(){
        StringBuilder oauth_link = new StringBuilder();
        oauth_link.append("https://kauth.kakao.com/oauth/authorize?");
        oauth_link.append("client_id=" + clientId);
        oauth_link.append("&redirect_uri=" + redirectUri);
        oauth_link.append("&response_type=code");

        return oauth_link;
    }

    public Page<Club> getClubPageByKeyword(String keyword, Pageable pageable){
        return clubRepository.findByKeyword(keyword, pageable);
    }

    public Account findAccountWithHobbyAndCityById(Long id){
        return accountRepository.findAccountWithHobbyAndCityById(id);
    }

    public List<Club> findIndexManagers(Account account, boolean close){
        return clubRepository.findList5ByManagersContainingAndClosedOrderByPublishDateTimeDesc(account, close);
    }

    public List<Club> findIndexMembers(Account account, boolean close){
        return clubRepository.findList5ByMembersContainingAndClosedOrderByPublishDateTimeDesc(account, close);
    }

    public List<Club> findListContainAccount(boolean publish, boolean close){
        return clubRepository.findFirst9ByPublishedAndClosedOrderByPublishDateTimeDesc(publish, close);
    }
}
