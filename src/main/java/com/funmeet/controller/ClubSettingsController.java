package com.funmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.City;
import com.funmeet.domain.Club;
import com.funmeet.domain.Hobby;
import com.funmeet.form.CityForm;
import com.funmeet.form.ClubDescriptionForm;
import com.funmeet.form.ClubForm;
import com.funmeet.form.HobbyForm;
import com.funmeet.repository.CityRepository;
import com.funmeet.repository.ClubRepository;
import com.funmeet.repository.HobbyRepository;
import com.funmeet.service.CityService;
import com.funmeet.service.ClubService;
import com.funmeet.service.HobbyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public String studyImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Club club = clubService.getClubUpdate(account, path);
        clubService.updateClubImage(club, image);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }


    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentAccount Account account, @PathVariable String path) {
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
        System.out.println(hobbyForm.getHobbyTitle() + "check");
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
        List<String> allZones = cityRepository.findAll().stream().map(City::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
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

    @GetMapping("/club")
    public String clubSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/club";
    }

    @PostMapping("/club/publish")
    public String publishClub(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Club club = clubService.getClubUpdateStatus(account, path);
        clubService.publish(club);
        attributes.addFlashAttribute("message", "스터디를 공개했습니다.");
        return "redirect:/club/" + club.getEncodedPath()+ "/settings/club";
    }

    @PostMapping("/club/close")
    public String closeClub(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Club club = clubService.getClubUpdateStatus(account, path);
        clubService.close(club);
        attributes.addFlashAttribute("message", "스터디를 종료했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Club club = clubService.getClubUpdateStatus(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "30분 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }

        clubService.startRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Club club = clubService.getClubUpdate(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }

        clubService.stopRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
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
    public String removeClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubUpdateStatus(account, path);
        clubService.remove(club);
        return "redirect:/";
    }


}
