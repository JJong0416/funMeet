package com.funmeet.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @NoArgsConstructor
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

    private String hobbies;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
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
