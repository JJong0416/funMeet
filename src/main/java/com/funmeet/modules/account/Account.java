package com.funmeet.modules.account;

import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @ToString
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private boolean kakaoTokenVerified;

    private String kakaoEmail;

    private String shortBio;

    private String occupation;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    // 다대다 관계 -> 취미 개수 로직 => 실무에서 터질 수 있다 => OneToMany <-> ManyToOne으로 바꿔야 했는데,
    // 리팩토링 하는 과정에서 의심해서
    @ManyToMany
    private Set<Hobby> hobby = new HashSet<>();

    @ManyToMany
    private Set<City> city = new HashSet<>();

    private String location;

    private String profileImage;

    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb = true;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb = true;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb = true;

    @Builder
    public Account(String nickname, String password, String email, String shortBio) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.shortBio = shortBio;
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public void completeProfile(String bio, String image) {
        this.shortBio = bio;
        this.profileImage = image;
    }

    public void updatePassword(String encodePassword) {
        this.password = encodePassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateNotification(NotificationForm notificationForm) {
        this.meetCreatedByEmail = notificationForm.isMeetCreatedByEmail();
        this.meetCreatedByWeb = notificationForm.isMeetCreatedByWeb();
        this.meetEnrollmentResultByEmail = notificationForm.isMeetEnrollmentResultByEmail();
        this.meetEnrollmentResultByWeb = notificationForm.isMeetEnrollmentResultByWeb();
        this.meetUpdateByEmail = notificationForm.isMeetUpdateByEmail();
        this.meetUpdatedByWeb = notificationForm.isMeetUpdatedByWeb();
    }
}
