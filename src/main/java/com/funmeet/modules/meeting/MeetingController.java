package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.club.Club;
import com.funmeet.modules.club.ClubService;
import com.funmeet.modules.mapper.MeetingMapper;
import com.funmeet.modules.meeting.form.MeetingForm;
import com.funmeet.modules.meeting.validator.MeetingFormValidator;
import lombok.RequiredArgsConstructor;
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
    private final MeetingFormValidator meetingFormValidator;
    private final MeetingRepository meetingRepository;
    private final EnrollmentRepository enrollmentRepository;

    @InitBinder("meetForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(meetingFormValidator);
    }

    @GetMapping("/create-meeting")
    public String newMeetForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute("meetingForm",new MeetingForm());
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
        Meeting meeting = meetingService.createMeeting(account, club, MeetingMapper.INSTANCE.MeetingFormToEntity(meetingForm));
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
        model.addAttribute(MeetingMapper.INSTANCE.MeetingToMeetingForm(meeting));
        return "meeting/update";
    }


    @PostMapping("/meeting/{id}/edit")
    public String updateMeetingSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("id") Meeting meeting, @Valid MeetingForm meetingForm, Errors errors,
                                    Model model) {
        Club club = clubService.getClubUpdate(account, path);
        meetingFormValidator.validateUpdateForm(meetingForm, meeting, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute(meeting);
            return "meeting/update";
        }

        meetingService.updateMeeting(meeting, meetingForm);
        return "redirect:/club/" + club.getEncodedPath() +  "/meeting/" + meeting.getId();
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

    /* 순환참조 규칙때문에 어쩔수없이 ClubSettingsControl에 작업해야하지만, Meeting컨트롤러에서 작업 */
    /* 이렇게 나온 이유는 설계를 못한 잘못이 아닌 trade-off를 극복하기 위한 한가지 방법. */
    /* 또한, 그냥 종료시키면 Meeting도 다 종료시켜버릴 수 있지만, 중요한 데이터 남아있을 수 있기 때문에, 미팅이 남아있다고 알람만 때린다.*/
    @PostMapping("/settings/club/remove")
    public String checkMeetingWithClubRemove(@CurrentAccount Account account, @PathVariable String path, Model model){

        Club club = clubService.getClubUpdateStatus(account,path);
        List<Meeting> meetings = meetingRepository.findByClub(club);

        if (meetings.size() != 0){
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute("message", "fail_clubRemove");
            return "club/settings/club";
        }
        clubService.close(club);
        clubService.remove(club);
        return "redirect:/";
    }
}
