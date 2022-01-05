package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.club.form.ClubForm;
import com.funmeet.modules.club.validator.ClubFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class ClubController {

    private final ClubFormValidator clubFormValidator;
    private final ClubService clubService;

    @InitBinder("clubForm")
    public void clubFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(clubFormValidator);
    }

    @GetMapping("/create-club")
    public String createClub(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("clubForm",new ClubForm());
        return "club/form";
    }

    @PostMapping("/create-club")
    public String createClubForm(@CurrentAccount Account account, @Valid ClubForm clubForm, Errors errors,Model model){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "club/form";
        }

        Club newClub = clubService.createNewClub(account,clubForm);
        return "redirect:/club/" + URLEncoder.encode(newClub.getClubPath(),StandardCharsets.UTF_8);
    }

    @GetMapping("/club/{path}")
    public String viewClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubOnlyByPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/page";
    }

    @GetMapping("/club/{path}/members")
    public String viewClubMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubOnlyByPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/members";
    }

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.addMember(account,path);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    public String leaveClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.removeMember(account,path);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }
}
