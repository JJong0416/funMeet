package com.funmeet.controller;


import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.form.NicknameForm;
import com.funmeet.form.NotificationForm;
import com.funmeet.form.PasswordForm;
import com.funmeet.form.Profile;
import com.funmeet.service.AccountService;
import com.funmeet.validator.NicknameValidator;
import com.funmeet.validator.PasswordFormValidation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final ModelMapper modelMapper;


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
    @GetMapping("/settings/hobby")
    public String updateTags(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "settings/hobby";
    }

    /* 취미 */



    /* 취미 끝*/

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
}
