package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.CurrentAccount;
import com.funmeet.modules.club.form.ClubForm;
import com.funmeet.modules.club.validator.ClubFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
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
@Slf4j
public class ClubController {

    private final ClubService clubService;
    private final ClubFormValidator clubFormValidator;
    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    @InitBinder("clubForm")
    public void clubFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(clubFormValidator);
    }

    @GetMapping("/create_club")
    public String createClub(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("clubForm",new ClubForm());
        return "club/form";
    }

    @PostMapping("/create_club")
    public String createClubForm(@CurrentAccount Account account, @Valid ClubForm clubForm, Errors errors,Model model){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "club/form";
        }

        Club newClub = clubService.createNewClub(modelMapper.map(clubForm, Club.class),account);
        return "redirect:/club/" + URLEncoder.encode(newClub.getClubPath(),StandardCharsets.UTF_8);
    }

    @GetMapping("/club/{path}")
    public String viewClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findByClubPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/page";
    }

    @GetMapping("/club/{path}/members")
    public String viewClubMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findByClubPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/members";
    }

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        stopWatch.stop();
        log.info("수행시간 >> {}", stopWatch.getTotalTimeSeconds());  // 수행시간 >> 5.866
        clubService.addMember(club, account);

        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    public String leaveClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        clubService.removeMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    /* Club 만들기 위한 Test Controller */
    @GetMapping("/club/data")
    public String generateTestData(@CurrentAccount Account account){
        clubService.generateTestClub(account);
        return "redirect:/";
    }
}
