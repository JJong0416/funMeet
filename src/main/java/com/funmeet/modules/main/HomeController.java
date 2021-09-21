package com.funmeet.modules.main;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;

    @Value("${authURL.client_id}")
    private String client_id;

    @Value("${authURL.redirect_uri}")
    private String redirect_uri;

    private final AppProperties appProperties;

    @GetMapping({"","/"})
    public String home(@CurrentAccount Account account, Model model){
        if (account != null){
            Account accountLoaded = accountRepository.findAccountWithHobbyAndCityById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("clubList", clubRepository.findByAccount(
                    accountLoaded.getHobby(),
                    accountLoaded.getCity()));
            model.addAttribute("clubManagerOf",
                    clubRepository.findList5ByManagersContainingAndClosedOrderByPublishDateTimeDesc(account, false));
            model.addAttribute("clubMemberOf",
                    clubRepository.findList5ByMembersContainingAndClosedOrderByPublishDateTimeDesc(account, false));

            return "index_with_login";
        }
        model.addAttribute("clubList", clubRepository.findFirst9ByPublishedAndClosedOrderByPublishDateTimeDesc(true, false));
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model){
        StringBuilder oauth_link = new StringBuilder();
        oauth_link.append("https://kauth.kakao.com/oauth/authorize?");
        oauth_link.append("client_id=" + client_id);
        oauth_link.append("&redirect_uri=" + redirect_uri);
        oauth_link.append("&response_type=code");

        model.addAttribute("link",oauth_link);

        return "login";
    }

    @GetMapping("/search")
    public String searchClub(String keyword, Model model,
                             @PageableDefault(size = 6, sort = "publishDateTime", direction = Sort.Direction.DESC)
                                     Pageable pageable)  {
        Page<Club> clubList = clubRepository.findByKeyword(keyword, pageable);
        model.addAttribute("clubList",clubList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishDateTime") ? "publishDateTime" : "memberCount");

        return "search";
    }
}