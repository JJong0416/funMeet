package com.funmeet.domain;

import com.funmeet.adaptor.AdaptAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Meeting {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account createdAccount;

    @ManyToOne
    private Club club;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private Integer limitOfEnrollments;

    @Column(nullable = false)
    private Integer meetingPrice;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @OneToMany(mappedBy = "meeting")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    public boolean isEnrollable(AdaptAccount adaptAccount) {
        return isNotClosed() && !isAlreadyEnrolled(adaptAccount);
    }

    public boolean isDisEnrollable(AdaptAccount adaptAccount) {
        return isNotClosed() && isAlreadyEnrolled(adaptAccount);
    }

    private boolean isNotClosed() {
        return this.endDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isAttended(AdaptAccount adaptAccount) {
        Account account = adaptAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyEnrolled(AdaptAccount adaptAccount) {
        Account account = adaptAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    public int numberOfRemain(){
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
}
