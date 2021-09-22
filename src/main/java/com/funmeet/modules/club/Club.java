package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.AdaptAccount;
import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    private Set<Account> managers  = new HashSet<>();

    @ManyToMany
    private Set<Account> members  = new HashSet<>();

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String banner;

    private int memberCount;

    @ManyToMany
    private Set<Hobby> hobby  = new HashSet<>();

    @ManyToMany
    private Set<City> city  = new HashSet<>();

    private LocalDateTime publishDateTime;

    private LocalDateTime closedDateTime;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
        this.memberCount++;
    }

    public void addMember(Account account) {
        this.members.add(account);
        this.memberCount++;
    }

    public boolean isJoinable(AdaptAccount adaptAccount) {
        Account account = adaptAccount.getAccount();
        return this.isPublished() && !this.members.contains(account) && !this.managers.contains(account);
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

    public boolean isPublish() {
        return (this.getHobby().size() != 0  && this.getCity().size() != 0);
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.clubPath, StandardCharsets.UTF_8);
    }

    public boolean isManagerOfBy(Account account) {
        return this.getManagers().contains(account);
    }
}
