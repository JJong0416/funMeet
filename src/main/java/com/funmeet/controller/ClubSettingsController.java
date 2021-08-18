package com.funmeet.controller;

import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import com.funmeet.form.ClubDescriptionForm;
import com.funmeet.repository.ClubRepository;
import com.funmeet.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/club/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingsController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("/description")
    public String viewStudySetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));
        return "club/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid ClubDescriptionForm studyDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubUpdate(account,path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "club/settings/description";
        }

        clubService.updateClub_fullDescription(club, studyDescriptionForm);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/club/" + getPath(path) + "/settings/description";
    }

}
