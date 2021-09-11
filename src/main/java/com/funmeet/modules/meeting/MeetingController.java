package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.CurrentAccount;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubService;
import com.funmeet.modules.meeting.form.MeetingForm;
import com.funmeet.modules.meeting.validator.MeetingFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/club/{path}")
public class MeetingController {

    private final ClubService clubService;
    private final MeetingService meetingService;
    private final ModelMapper modelMapper;
    private final MeetingFormValidator meetingFormValidator;
    private final MeetingRepository meetingRepository;
    private final EnrollmentRepository enrollmentRepository;

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

    @GetMapping("/meeting")
    public String viewClubMain(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);

        List<Meeting> meetings = meetingRepository.findByClubOrderByStartDateTime(club);
        List<Meeting> newMeetings = new ArrayList<>();
        List<Meeting> oldMeetings = new ArrayList<>();

        meetings.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldMeetings.add(e);
            } else {
                newMeetings.add(e);
            }
        });

        model.addAttribute("newMeetings", newMeetings);
        model.addAttribute("oldMeetings", oldMeetings);

        return "club/meeting";
    }

    @GetMapping("/meeting/{id}")
    public String viewMeet(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(clubService.getClub(path));
        model.addAttribute(meetingRepository.findById(id).orElseThrow());
        return "meeting/page";
    }


    @GetMapping("/meeting/{id}/edit")
    public String updateMeetingForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable Long id, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        Meeting meeting = meetingRepository.findById(id).orElseThrow();
        model.addAttribute(club);
        model.addAttribute(meeting);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(meeting, MeetingForm.class));
        return "meeting/update";
    }


    @PostMapping("/meeting/{id}/edit")
    public String updateMeetingSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable Long id, @Valid MeetingForm meetingForm, Errors errors,
                                    Model model) {
        Club club = clubService.getClubUpdate(account, path);

        Meeting meeting = meetingRepository.findById(id).orElseThrow();
        meetingForm.setMeetingType(meeting.getMeetingType());
        meetingFormValidator.validateUpdateForm(meetingForm, meeting, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute(meeting);
            return "meeting/update";
        }

        meetingService.updateMeeting(meeting, meetingForm);
        return "redirect:/club/" + club.getEncodedPath() +  "/club/" + meeting.getId();
    }

    @PostMapping("/meeting/{id}/delete")
    public String cancelMeeting(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getClubUpdateStatus(account, path);
        meetingService.deleteMeeting(meetingRepository.findById(id).orElseThrow());
        return "redirect:/club/" + club.getEncodedPath() + "/meeting";
    }

    @PostMapping("/meeting/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getClubToEnroll(path);
        Meeting meeting = meetingRepository.findById(id).orElseThrow();
        meetingService.newEnrollment(meeting,account);

        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + id;
    }

    @PostMapping("/meeting/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account,
                                   @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getClubToEnroll(path);
        Meeting meeting = meetingRepository.findById(id).orElseThrow();

        meetingService.cancelEnrollment(meeting,account);

        return "redirect:/club/" + club.getEncodedPath() +  "/meeting/" + id;
    }

    @GetMapping("/meeting/{meetingId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollmentAccount(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable Long meetingId, @PathVariable Long enrollmentId) { // check
        Club club = clubService.getClubUpdateStatus(account,path);
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        meetingService.acceptEnrollment(meeting,enrollment);

        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }

    @GetMapping("/meeting/{meetingId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollmentAccount(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable Long meetingId, @PathVariable Long enrollmentId) {

        Club club = clubService.getClubUpdateStatus(account,path);
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        meetingService.rejectEnrollment(meeting,enrollment);

        return "redirect:/club/" + club.getEncodedPath() + "/meeting/" + meeting.getId();
    }
}
