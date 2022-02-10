package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubService;
import com.funmeet.modules.meeting.form.MeetingForm;
import com.funmeet.modules.meeting.validator.MeetingFormValidator;
import lombok.RequiredArgsConstructor;
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
    private final MeetingFormValidator meetingFormValidator;

    @InitBinder("meetForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(meetingFormValidator);
    }

    @GetMapping("/create-meeting")
    public String newMeetForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute("meetingForm", new MeetingForm());
        return "meeting/form";
    }

    @PostMapping("/create-meeting")
    public String newMeetSubmit(@CurrentAccount Account account, @PathVariable String path,
                                @Valid MeetingForm meetingForm, Errors errors, Model model) {

        Club club = clubService.getClubUpdateStatus(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "meeting/form";
        }

        Meeting meeting = meetingService.createMeeting(account, club, meetingForm);
        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }

    @GetMapping("/meeting")
    public String viewClubMain(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute("newMeetings", meetingService.getNewMeeting(club));
        model.addAttribute("oldMeetings", meetingService.getOldMeeting(club));

        return "club/meeting";
    }

    @GetMapping("/meeting/{id}")
    public String viewMeet(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(clubService.getClub(path));
        model.addAttribute(meetingService.getMeetingById(id));
        return "meeting/page";
    }


    @GetMapping("/meeting/{id}/edit")
    public String updateMeetingForm(@CurrentAccount Account account,
                                    @PathVariable String path, @PathVariable Long id, Model model) {
        Meeting meeting = meetingService.getMeetingById(id);

        model.addAttribute(meetingService.meetingToMeetingForm(meeting));
        model.addAttribute(clubService.getClubUpdate(account, path));
        model.addAttribute(meeting);
        model.addAttribute(account);
        return "meeting/update";
    }


    @PostMapping("/meeting/{id}/edit")
    public String updateMeetingSubmit(@CurrentAccount Account account, @PathVariable String path,
                                      @PathVariable Long id, @Valid MeetingForm meetingForm, Errors errors,
                                      Model model) {
        Club club = clubService.getClubUpdate(account, path);
        Meeting meeting = meetingService.getMeetingWithValidation(id, meetingForm, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute(meeting);
            return "meeting/update";
        }

        meetingService.updateMeeting(meeting, meetingForm);
        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }

    @PostMapping("/meeting/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {

        Club club = clubService.getClubToEnroll(path);
        meetingService.newEnrollment(account, id);
        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + id;
    }

    @PostMapping("/meeting/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account,
                                   @PathVariable String path, @PathVariable Long id) {

        Club club = clubService.getClubToEnroll(path);
        meetingService.cancelEnrollment(account, id);
        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + id;
    }

    @PostMapping("/meeting/{id}/delete")
    public String cancelMeeting(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {

        Club club = clubService.getClubUpdateStatus(account, path);
        meetingService.deleteMeeting(id);
        return "redirect:/club/" + club.getEncodedPath() + "/meeting";
    }

    @GetMapping("/meeting/{meetingId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollmentAccount(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable Long meetingId, @PathVariable Long enrollmentId) {

        Club club = clubService.getClubUpdateStatus(account, path);
        Meeting meeting = meetingService.acceptEnrollment(meetingId, enrollmentId);

        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }

    @GetMapping("/meeting/{meetingId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollmentAccount(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable Long meetingId, @PathVariable Long enrollmentId) {

        Club club = clubService.getClubUpdateStatus(account, path);
        Meeting meeting = meetingService.rejectEnrollment(meetingId, enrollmentId);

        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }
}