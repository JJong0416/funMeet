package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Account account;

    @Column(nullable = false)
    private String shortIntroduce;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

}
