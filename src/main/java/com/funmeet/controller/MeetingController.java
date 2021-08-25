package com.funmeet.controller;

import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import com.funmeet.domain.Meeting;
import com.funmeet.form.MeetingForm;
import com.funmeet.repository.MeetingRepository;
import com.funmeet.service.ClubService;
import com.funmeet.service.MeetingService;
import com.funmeet.validator.MeetingFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/club/{path}")
public class MeetingController {

    private final ClubService clubService;
    private final MeetingService meetingService;
    private final ModelMapper modelMapper;
    private final MeetingFormValidator meetingFormValidator;
    private final MeetingRepository meetingRepository;

    @InitBinder("meetForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(meetingFormValidator);
    }

    @GetMapping("/create_meeting")
    public String newMeetForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute("meetingForm",new MeetingForm());
        return "meeting/form";
    }

    @PostMapping("/create_meeting")
    public String newMeetSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid MeetingForm meetingForm, Errors errors, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "meeting/form";
        }

        Meeting meeting = meetingService.createMeeting(account, club, modelMapper.map(meetingForm, Meeting.class));
        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }

    @GetMapping("/meeting/{id}")
    public String viewMeet(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(clubService.getClub(path));
        model.addAttribute(meetingRepository.findById(id).orElseThrow());
        return "meeting/page";
    }


}
