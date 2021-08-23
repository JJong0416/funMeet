package com.funmeet.controller;

import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
import com.funmeet.domain.Meeting;
import com.funmeet.form.MeetingForm;
import com.funmeet.service.ClubService;
import com.funmeet.service.MeetingService;
import com.funmeet.validator.MeetingFormValidator;
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

@Controller
@RequiredArgsConstructor
public class MeetingController {

    private final ClubService clubService;
    private final MeetingService meetingService;
    private final ModelMapper modelMapper;
    private final MeetingFormValidator meetingFormValidator;

    @InitBinder("meetForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(meetingFormValidator);
    }

    @GetMapping("/create_meet")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute(new MeetingForm());
        return "meeting/form";
    }

    @PostMapping("/create_meet")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
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


}
