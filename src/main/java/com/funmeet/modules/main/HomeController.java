package com.funmeet.modules.main;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.CurrentAccount;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClubRepository clubRepository;

    @GetMapping({"","/"})
    public String home(@CurrentAccount Account account, Model model){
        if (account != null){
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(){
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