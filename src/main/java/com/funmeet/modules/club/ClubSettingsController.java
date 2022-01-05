package com.funmeet.modules.club;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityForm;
import com.funmeet.modules.city.CityService;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.mapper.ClubMapper;
import com.funmeet.modules.meeting.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/club/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingsController {

    private final ClubService clubService;
    private final HobbyService hobbyService;
    private final CityService cityService;

    @GetMapping("/description")
    public String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(clubService.mappingDescriptionForm(club));
        return "club/settings/description";
    }

    @PostMapping("/description")
    public String updateClubInfo(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid ClubDescriptionForm clubDescriptionForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {

        Club club = clubService.getClubUpdate(account,path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "club/settings/description";
        }

        clubService.updateClubDescription(club, clubDescriptionForm);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String clubBannerForm(@CurrentAccount Account account, @PathVariable String path, Model model){

        Club club = clubService.getClubUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/banner";
    }

    @PostMapping("/banner")
    public String clubImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                  String image, RedirectAttributes attributes) {

        Club club = clubService.updateClubImage(account, path, image);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }


    @PostMapping("/banner/enable")
    public String enableClubBanner(@CurrentAccount Account account, @PathVariable String path) {

        Club club = clubService.checkUseClubBanner(account, path, true);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableClubBanner(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubService.checkUseClubBanner(account, path, false);
        return "redirect:/club/" + club.getEncodedPath()+ "/settings/banner";
    }

    @GetMapping("/hobby")
    public String clubHobbyForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute("hobby", clubService.getClubHobby(account, path));
        model.addAttribute("whitelist", clubService.getWhiteListHobby());

        return "club/settings/hobby";
    }

    @PostMapping("/hobby/add")
    @ResponseBody
    public ResponseEntity addHobby(@CurrentAccount Account account, @PathVariable String path,
                                   @RequestBody HobbyForm hobbyForm) {
        clubService.addHobby(account, path,hobbyForm.getHobbyTitle());
        Club club = clubService.getClub(path);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hobby/remove")
    @ResponseBody
    public ResponseEntity removeHobby(@CurrentAccount Account account, @PathVariable String path,
                                      @RequestBody HobbyForm hobbyForm) {
        clubService.removeHobby(account, path, hobbyForm.getHobbyTitle());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/city")
    public String clubCityForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute("city", clubService.getClubCity(account, path));
        model.addAttribute("whitelist", clubService.getWhiteListCity());
        return "club/settings/city";
    }

    @PostMapping("/city/add")
    @ResponseBody
    public ResponseEntity addCity(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody CityForm cityForm) {

        clubService.addCity(account, path, cityForm.getKrCity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/city/remove")
    @ResponseBody
    public ResponseEntity removeCity(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody CityForm cityForm) {
        clubService.removeCity(account, path, cityForm.getKrCity());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/club")
    public String clubSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/club";
    }

    @PostMapping("/club/publish")
    public String publishClub(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Club club = clubService.getClubUpdateStatus(account, path);

        if (!clubService.isPublish(account, club)){
            model.addAttribute("message","fail_clubPublish");
            model.addAttribute(account);
            model.addAttribute(club);
            return "club/settings/club";
        }

        clubService.publish(club);
        model.addAttribute("message","success_clubPublish");
        return "redirect:/club/" + club.getEncodedPath()+ "/settings/club";
    }

    @PostMapping("/club/path")
    public String updateClubPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                 Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubUpdateStatus(account, path);

        if (!clubService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute("clubPathError", "해당 모임 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "club/settings/club";
        }

        clubService.updateClubPath(club, newPath);
        attributes.addFlashAttribute("message", "모임 경로를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/title")
    public String updateClubTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                  Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubUpdateStatus(account, path);
        
        if (!clubService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute("ClubTitleError", "스터디 이름을 다시 입력하세요.");
            return "club/settings/club";
        }

        clubService.updateClubTitle(club, newTitle);
        attributes.addFlashAttribute("message", "모임 이름을 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/remove")
    public String checkMeetingWithClubRemove(@CurrentAccount Account account, @PathVariable String path, Model model){

        Club club = clubService.getClubUpdateStatus(account,path);

        if (clubService.isNotZeroMeetings(club)){
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute("message", "fail_clubRemove");
            return "club/settings/club";
        }

        clubService.removeClub(club);
        return "redirect:/";
    }
}
