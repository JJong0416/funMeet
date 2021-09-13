package com.funmeet.modules.club;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.CurrentAccount;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityForm;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.city.CityService;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final HobbyRepository hobbyRepository;
    private final CityRepository cityRepository;
    private final ClubRepository clubRepository;

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));
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
        Club club = clubService.getClubUpdate(account, path);
        clubService.updateClubImage(club, image);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }


    @PostMapping("/banner/enable")
    public String enableClubBanner(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getClubUpdate(account, path);
        clubService.enableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableClubBanner(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubService.getClubUpdate(account,path);
        clubService.disableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath()+ "/settings/banner";
    }

    @GetMapping("/hobby")
    public String clubHobbyForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);

        model.addAttribute("hobby", club.getHobby().stream()
                .map(Hobby::getTitle).collect(Collectors.toList()));
        List<String> allHobbyTitles = hobbyRepository.findAll().stream()
                .map(Hobby::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allHobbyTitles));
        return "club/settings/hobby";
    }

    @PostMapping("/hobby/add")
    @ResponseBody
    public ResponseEntity addHobby(@CurrentAccount Account account, @PathVariable String path,
                                   @RequestBody HobbyForm hobbyForm) {

        Club club = clubService.getClubUpdateHobby(account, path);
        Hobby hobby = hobbyService.findOrCreateHobby(hobbyForm.getHobbyTitle());
        clubService.addHobby(club, hobby);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hobby/remove")
    @ResponseBody
    public ResponseEntity removeHobby(@CurrentAccount Account account, @PathVariable String path,
                                      @RequestBody HobbyForm hobbyForm) {
        Club club = clubService.getClubUpdateHobby(account, path);
        Hobby hobby = hobbyRepository.findByTitle(hobbyForm.getHobbyTitle()).orElseThrow();

        if (hobby == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeHobby(club, hobby);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/city")
    public String clubCityForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute("city", club.getCity().stream()
                .map(City::toString).collect(Collectors.toList()));
        List<String> allCity = cityRepository.findAll().stream().map(City::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allCity));
        return "club/settings/city";
    }

    @PostMapping("/city/add")
    @ResponseBody
    public ResponseEntity addCity(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody CityForm cityForm) {

        Club club = clubService.getClubUpdateCity(account, path);
        City city = cityRepository.findByKrCity(cityForm.getKrCity());

        if (city == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.addCity(club, city);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/city/remove")
    @ResponseBody
    public ResponseEntity removeCity(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody CityForm cityForm) {
        Club club = clubService.getClubUpdateCity(account, path);
        City city = cityRepository.findByKrCity(cityForm.getKrCity());
        if (city == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeCity(club, city);
        return ResponseEntity.ok().build();
    }

    /* 밑에는 모임 관리  */

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

        if (!club.isPublish()){
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
}
