package com.funmeet.modules.alert;

import com.funmeet.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Alert {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String message;

    private String link;

    private LocalDateTime createdLocalDateTime;

    private boolean checked;

    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private AlertType alertType;

}
