package com.funmeet.modules.account;

import com.funmeet.modules.account.security.AdaptAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public void loginByAccount(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AdaptAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void loginByEmail(String email){
        Account account = accountRepository.findByEmail(email).orElseThrow( () -> {
            throw new UsernameNotFoundException(email);
        });

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AdaptAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String findAccount) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(findAccount).orElseGet( () ->
                accountRepository.findByNickname(findAccount).orElseThrow( () -> {
                    throw new UsernameNotFoundException(findAccount);
                }));
        return new AdaptAccount(account);
    }

    public void logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
    }
}
