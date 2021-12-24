package com.funmeet.modules.account;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.form.NicknameForm;
import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.account.form.PasswordForm;
import com.funmeet.modules.account.form.Profile;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.account.validator.NicknameValidator;
import com.funmeet.modules.account.validator.PasswordFormValidation;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AccountSettingsController {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final HobbyService hobbyService;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidation());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

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

    @GetMapping("/settings/hobby")
    public String updateHobby(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Hobby> hobby = accountService.getHobby(account);
        model.addAttribute("hobby",hobby.stream().map(Hobby::getTitle).collect(Collectors.toList()));

        List<String> allHobby = accountService.getAllHobby(account);
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allHobby));

        return "settings/hobby";
    }


    @PostMapping("/settings/hobby/add")
    @ResponseBody
    public ResponseEntity addHobby(@CurrentAccount Account account, @RequestBody HobbyForm hobbyForm){
        String title = hobbyForm.getHobbyTitle();
        Hobby hobby = hobbyService.findOrCreateHobby(hobbyForm.getHobbyTitle());
        accountService.addHobby(account, hobby);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/hobby/remove")
    @ResponseBody
    public ResponseEntity removeHobby(@CurrentAccount Account account, @RequestBody HobbyForm hobbyForm) {
        String title = hobbyForm.getHobbyTitle();
        Hobby hobby = accountService.findHobbyByTitle(title);

        if (hobby == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeHobby(account,hobby);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/location")
    public String updateHobbyForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<City> city = accountService.getCity(account);
        model.addAttribute("city", city.stream().map(City::toString).collect(Collectors.toList()));

        List<String> allCity = accountService.getAllCity(account);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allCity));

        return "settings/location";
    }

    @PostMapping("/settings/location/add")
    public ResponseEntity addCity(@CurrentAccount Account account, @RequestBody CityForm cityForm){
        City city = accountService.findCityByKrCity(cityForm.getKrCity());

        if (city == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.addCity(account,city);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/location/remove")
    public ResponseEntity removeCity(@CurrentAccount Account account, @RequestBody CityForm cityForm){
        City city = accountService.findCityByKrCity(cityForm.getKrCity());

        if (city == null){
            return ResponseEntity.badRequest().build();
        }
        accountService.removeCity(account,city);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/notification")
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notification",AccountMapper.INSTANCE.AccountToNotificationForm(account));
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
    public String updateSecurity(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("passwordForm",new PasswordForm());
        return "settings/account";
    }

    @GetMapping("/settings/account")
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("nicknameForm",new NicknameForm());
        model.addAttribute("passwordForm",new PasswordForm());
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

    @PostMapping("/settings/security")
    public String updateSecurity(@CurrentAccount Account account, @Valid PasswordForm passwordForm,
                                 Errors errors, Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "성공");
        return "redirect:" + "/settings/account";
    }

    @PostMapping("/settings/delete")
    public String deleteAccount(@CurrentAccount Account account, HttpServletRequest request){
        accountService.deleteAccount(account);
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }
}
