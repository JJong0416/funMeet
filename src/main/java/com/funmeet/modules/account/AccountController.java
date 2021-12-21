package com.funmeet.modules.account;


import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        System.out.println("hello1");
        webDataBinder.addValidators(signUpFormValidator);
        System.out.println("hello2");
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if (errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = accountService.processSignUpAccount(signUpForm);
        System.out.println("heelo234");
        System.out.println(account);
        accountService.login(account);
        System.out.println("345");
        return "redirect:/";
    }

    @GetMapping("/certification-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "email/certification-email";
    }

    @GetMapping("/resend-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 30분에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "email/certification-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token") // 이메일 토큰 눌렀을 때 뜨는
    public String checkEmailToken(String token, String email, Model model){

        Account account = accountService.findAccountByEmail(email);
        String viewUrl = "email/check-email";

        if (account == null){
            model.addAttribute("error","wrong.email");
            return viewUrl;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error","wrong.token");
            return viewUrl;
        }

        accountService.completeSignUp(account);
        model.addAttribute("nickname",account.getNickname());
        return viewUrl;
    }

    @GetMapping("/find-account")
    public String emailLoginForm() {
        return "email/find-account";
    }

    @PostMapping("/find-account")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {

        Account account = accountService.findAccountByEmail(email);

        if (account == null || !account.canSendConfirmEmail()) {
            if (account == null) {
                model.addAttribute("error", "유효");
                return "email/find-account";
            }

            model.addAttribute("error", "시간");
            return "email/find-account";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/find-account";
    }

    @GetMapping("/auth-email")
    public String passByEmail(String token, String email, Model model) {

        Account account = accountService.findAccountByEmail(email);
        String view = "email/auth-email";

        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        accountService.login(account);
        return view;
    }



    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account){
        Account viewAccount = accountService.findAccountByNickname(nickname);

        if (viewAccount == null){
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다");
        }

        model.addAttribute("account",viewAccount);
        model.addAttribute("isOwner",viewAccount.equals(account));
        model.addAttribute("cityList",viewAccount.getCity());

        return "account/profile";
    }

}
