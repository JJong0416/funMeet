package com.funmeet.modules.account;

import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id") // id만 사용하는 이유는 연관관계 복잡해질때, 순환참조 막기 위해서
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private String short_bio;

    private String occupation;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean kakaoVerified;

    private String kakaoCheckToken;

    private LocalDateTime joinedAt;

    @ManyToMany
    private List<Hobby> hobby = new ArrayList<>();

    @ManyToMany
    private List<City> city = new ArrayList<>();

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

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }
}
