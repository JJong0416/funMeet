package com.funmeet.modules.alarm;

import com.funmeet.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @NoArgsConstructor
@Getter @EqualsAndHashCode(of = "id")
public class Alarm {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    private String link;

    private LocalDateTime createdDateTime;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    private AlarmType AlarmType;

    public void updateCheck(boolean checked){
        this.checked = checked;
    }

    @Builder
    public Alarm(String title, String message, String link, LocalDateTime createdDateTime,
                 boolean checked, Account account, AlarmType AlarmType){
        this.title = title;
        this.message = message;
        this.link = link;
        this.createdDateTime = createdDateTime;
        this.checked = checked;
        this.account = account;
        this.AlarmType = AlarmType;
    }
}
