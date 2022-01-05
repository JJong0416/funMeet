package com.funmeet.modules.enrollment;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.meeting.Meeting;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
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

    public void updateAccepted(boolean check){
        this.accepted = check;
    }

    public void updateMeeting(Meeting meeting){
        this.meeting = meeting;
    }
}
