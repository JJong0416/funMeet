package com.funmeet.modules.main;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
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
    private final HomeService homeService;

    @GetMapping({"", "/"})
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            Account accountLoaded = homeService.findAccountWithHobbyAndCityById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("interestedClubList", clubRepository.findClubByAccount(accountLoaded.getHobby(), accountLoaded.getCity()));
            model.addAttribute("clubManagerOf", homeService.findIndexManagers(account, false));
            model.addAttribute("clubMemberOf", homeService.findIndexMembers(account, false));
            return "index-with-login";
        }

        model.addAttribute("clubList", homeService.findListContainAccount(true, false));
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        StringBuilder oauth_link = homeService.getOAuthLink();
        model.addAttribute("link", oauth_link);
        return "login";
    }

    @GetMapping("/search")
    public String searchClub(String keyword, Model model,
                             @PageableDefault(size = 6, sort = "publishDateTime", direction = Sort.Direction.DESC)
                                     Pageable pageable) {
        Page<Club> clubList = homeService.getClubPageByKeyword(keyword, pageable);
        model.addAttribute("clubList", clubList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishDateTime") ? "publishDateTime" : "memberCount");

        return "search";
    }
}