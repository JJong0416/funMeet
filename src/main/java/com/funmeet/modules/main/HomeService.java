package com.funmeet.modules.main;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.city.City;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import com.funmeet.modules.hobby.Hobby;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeService {

    @Value("${authURL.client_id}")
    private String client_id;

    @Value("${authURL.redirect_uri}")
    private String redirect_uri;

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final AppProperties appProperties;

    public StringBuilder getOAuthLink(){
        StringBuilder oauth_link = new StringBuilder();
        oauth_link.append("https://kauth.kakao.com/oauth/authorize?");
        oauth_link.append("client_id=" + client_id);
        oauth_link.append("&redirect_uri=" + redirect_uri);
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
