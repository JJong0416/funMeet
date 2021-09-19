package com.funmeet.modules.account;

import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    /* OAuth Token Field */
    private boolean kakaoTokenVerified;

    private String kakaoEmail;

    private String short_bio;

    private String occupation;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    @ManyToMany
    private Set<Hobby> hobby = new HashSet<>();

    @ManyToMany
    private Set<City> city = new HashSet<>();

    private String location;

    @Lob
    private String profileImage;

    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb = true;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb = true;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb = true;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public void completeOAuthSignup(String kakaoEmail){
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
        this.kakaoEmail = kakaoEmail;
        this.kakaoTokenVerified = true;
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }
}
