package com.funmeet.controller;

import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import com.funmeet.form.ClubForm;
import com.funmeet.repository.ClubRepository;
import com.funmeet.service.ClubService;
import com.funmeet.validator.ClubFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    public String viewStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findByClubPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/page";
    }

    @GetMapping("/club/{path}/members")
    public String viewStudyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findByClubPath(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/members";
    }

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubRepository.findByClubPath(path);
        clubService.addMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    public String leaveClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubRepository.findByClubPath(path);
        clubService.removeMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

}
