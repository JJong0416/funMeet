package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AdaptAccount;
import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Club {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String clubPath;

    private String title;

    @ManyToMany // 관리자
    private List<Account> managers  = new ArrayList<>();

    @ManyToMany // 여러 사람들이 여러개 취미에 들 수 있음
    private List<Account> members  = new ArrayList<>();

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String banner;

    @ManyToMany
    private List<Hobby> hobby  = new ArrayList<>();

    @ManyToMany
    private List<City> city  = new ArrayList<>();

    private LocalDateTime publishDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public void addMember(Account account) {
        this.members.add(account);
    }

    public boolean isJoinable(AdaptAccount adaptAccount) {
        Account account = adaptAccount.getAccount();
        return this.isPublished() && this.isRecruiting() && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(AdaptAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(AdaptAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public String getBanner() {
        return banner != null ? banner : "/images/default_banner.png";
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("취미가 종료되었거나, 출시 준비 상태입니다.");
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("취미가 종료되었거나, 출시 준비 상태입니다.");
        }
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 모임을 공개하거나 30분 후 다시 시도해보십시오.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 모임을 공개하거나 30분 후 다시 시도해보십시오.");
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published && this.recruitingTime == null || this.recruitingTime.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isRemovable() {
        return !this.published;
    }


    public void removeMember(Account account) {
        this.getMembers().remove(account);
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.clubPath, StandardCharsets.UTF_8);
    }

    public boolean isManagerOfBy(Account account) {
        return this.getManagers().contains(account);
    }
}