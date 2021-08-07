package com.funmeet.controller;


import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Hobby;
import com.funmeet.form.*;
import com.funmeet.repository.HobbyRepository;
import com.funmeet.service.AccountService;
import com.funmeet.validator.NicknameValidator;
import com.funmeet.validator.PasswordFormValidation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final ModelMapper modelMapper;
    private final HobbyRepository hobbyRepository;


    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidation());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }



    /* 프로필 */
    @GetMapping("/settings/profile")
    public String updateProfileForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("profile",new Profile(account));

        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentAccount Account account, @Valid Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message","성공");
        return "redirect:" + "/settings/profile";
    }
    /* 프로필  끝*/
    /* 취미 */

    @GetMapping("/settings/hobby")
    public String updateTags(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        List<Hobby> hobby = accountService.getHobby(account);
        model.addAttribute("hobby",hobby.stream().map(Hobby::getTitle).collect(Collectors.toList()));
        return "settings/hobby";
    }


    @PostMapping("/settings/hobby/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody HobbyForm hobbyForm){

        String title = hobbyForm.getHobbyTitle();
        Hobby hobby = hobbyRepository.findByTitle(title).orElseGet(() -> hobbyRepository.save(Hobby.builder()
                .title(hobbyForm.getHobbyTitle())
                .build()));

        accountService.addHobby(account, hobby);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/hobby/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody HobbyForm hobbyForm) {
        String title = hobbyForm.getHobbyTitle();

//        Optional<Hobby> hobby = hobbyRepository.findByTitle(title);
//
//        if (hobby.isEmpty()){
//            return ResponseEntity.badRequest().build();
//        }

        Hobby hobby = hobbyRepository.findByTitle(title).orElseThrow();

        if (hobby == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeHobby(account,hobby);
        return ResponseEntity.ok().build();
    }

    /* 취미 끝*/
    /* 알림 */
    @GetMapping("/settings/notification")
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notification",modelMapper.map(account,NotificationForm.class));
        return "settings/notification";
    }

    @PostMapping("/settings/notification")
    public String updateNotifications(@CurrentAccount Account account, @Valid NotificationForm notification, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/notification";
        }

        accountService.updateNotification(account, notification);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:" + "/settings/notification";
    }

    /* 알림 끝*/
    /* 보안 */
    @GetMapping("/settings/security")
    public String updateSecurityForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/security";
    }

    @PostMapping("/settings/security")
    public String updateSecurity(@CurrentAccount Account account, @Valid PasswordForm passwordForm,
                                    Errors errors, Model model, RedirectAttributes attributes) {


        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/security";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "성공");
        return "redirect:" + "/settings/security";
    }

    /* 보안 끝*/
    /* 계정 */

    @GetMapping("/settings/account")
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new NicknameForm());
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "성공");
        return "redirect:" + "/settings/account";
    }
    /* 계정 끝*/
}
