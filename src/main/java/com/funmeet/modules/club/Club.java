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
import java.util.Set;

@Entity @Builder
@Getter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor @ToString
public class Club {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String clubPath;

    private String title;

    @ManyToMany
    private Set<Account> managers;

    @ManyToMany
    private Set<Account> members;

    private String shortDescription;

    @Lob
    private String fullDescription;

    @Lob
    private String banner;

    private int memberCount;

    @ManyToMany
    private Set<Hobby> hobby;

    @ManyToMany
    private Set<City> city;

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
        return (this.getHobby().size() != 0 && this.getCity().size() != 0);
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

    public void addHobby(Hobby hobby) {
        this.hobby.add(hobby);
    }

    public void removeHobby(Hobby hobby) {
        this.hobby.remove(hobby);
    }

    public void addCity(City city) {
        this.city.add(city);
    }

    public void removeCity(City city) {
        this.city.remove(city);
    }

    public void updateClubBanner(boolean check) {
        this.useBanner = check;
    }

    public void updateBannerImage(String banner) {
        this.banner = banner;
    }

    public void updateClubPath(String clubPath) {
        this.clubPath = clubPath;
    }

    public void updateClubTitle(String title) {
        this.title = title;
    }

    public void updateClubIntroduce(String shortDescription, String fullDescription) {
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }


    public Club(Account account, String title, String clubPath,
                String shortDescription, String fullDescription) {
        this.title = title;
        this.clubPath = clubPath;
        this.addManager(account);
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }
}
