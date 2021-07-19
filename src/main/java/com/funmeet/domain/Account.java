package com.funmeet.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
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

    private boolean emailVerified;

    private String emailCheckToken;

    private boolean kakaoVerified;

    private String kakaoCheckToken;

    private LocalDateTime joinedAt;

    //private List<Hobby> hobbies;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb;

}
