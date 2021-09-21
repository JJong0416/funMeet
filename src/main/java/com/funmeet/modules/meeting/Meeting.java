package com.funmeet.modules.meeting;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.AdaptAccount;
import com.funmeet.modules.club.Club;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OrderBy("enrolledAt")
    @OneToMany(mappedBy = "meeting")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    public boolean isEnrollable(AdaptAccount adaptAccount) {
        return isNotClosed() && !isAttended(adaptAccount) && !isAlreadyEnrolled(adaptAccount);
    }

    public boolean isDisEnrollable(AdaptAccount adaptAccount) {
        return isNotClosed() && !isAttended(adaptAccount) && isAlreadyEnrolled(adaptAccount);
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

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setMeeting(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setMeeting(null);
    }

    public boolean isAbleToWaitingEnrollment() {
        return this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments() && this.meetingType == MeetingType.FCFSB;
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.meetingType == MeetingType.CONFIRM
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.meetingType == MeetingType.CONFIRM
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    private List<Enrollment> getWaitingList() {
        return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
    }

    public void acceptWaitingList() {
        if (this.isAbleToWaitingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(), waitingList.size());
            waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
        }
    }

    public void acceptNextWaitingEnrollment() {
        if (this.isAbleToWaitingEnrollment()) {
            Enrollment enrollmentToAccept = this.getConsecutiveWaitingEnrollment();
            if (enrollmentToAccept != null) {
                enrollmentToAccept.setAccepted(true);
            }
        }
    }

    private Enrollment getConsecutiveWaitingEnrollment() {
        for (Enrollment e : this.enrollments) {
            if (!e.isAccepted()) {
                return e;
            }
        }
        return null;
    }

    public void accept(Enrollment enrollment) {
        if (this.meetingType == MeetingType.CONFIRM
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        if (this.meetingType == MeetingType.CONFIRM) {
            enrollment.setAccepted(false);
        }
    }
}
