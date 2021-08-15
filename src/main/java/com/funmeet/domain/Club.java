package com.funmeet.domain;

import com.funmeet.adaptor.AdaptAccount;
import lombok.*;
import javax.persistence.*;
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
}
