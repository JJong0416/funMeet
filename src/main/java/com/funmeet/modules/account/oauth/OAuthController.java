package com.funmeet.modules.account.oauth;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountDetailsService;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final AccountRepository accountRepository;
    private final OAuthService oAuthService;
    private final AccountService accountService;
    private final AccountDetailsService accountDetailsService;
    private final OAuthFormValidator oAuthFormValidator;

    @InitBinder("oAuthForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(oAuthFormValidator);
    }

    @GetMapping("/oauth2/authorization/kakao")
    public String kakaoCallback(@RequestParam String code, Model model, HttpServletRequest request){


        OAuthToken oAuthToken = oAuthService.getAccessToken(code);
        KakaoProfile kakaoProfile = oAuthService.getProfile(oAuthToken);

        String kakaoEmail = kakaoProfile.kakao_account.email;
        Account account = oAuthService.findAccountByKakaoEmail(kakaoEmail);

        if (account == null){
            model.addAttribute("oauthForm",new OAuthForm());
            model.addAttribute("kakaoEmail",kakaoEmail);
            return "account/oauth-sign-up";
        }

        accountDetailsService.loginByAccount(account);
        return "redirect:/";
    }

    @PostMapping("/oauth-sign-up")
    public String signUpSubmit(@Valid @ModelAttribute OAuthForm oAuthForm, Errors errors,String kakaoEmail){
        if (errors.hasErrors()){
            return "account/oauth-sign-up";
        }
        Account account = accountService.saveOauthSignUp(oAuthForm,kakaoEmail);
        accountDetailsService.loginByAccount(account);
        return "redirect:/";
    }
}
