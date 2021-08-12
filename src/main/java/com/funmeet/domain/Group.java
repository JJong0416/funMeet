package com.funmeet.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@Builder @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Group {

    @Id @GeneratedValue
    private Long id;

    private String groupPath;

    private String title;

    @ManyToMany // 관리자
    private List<Account> managers;

    @ManyToMany // 여러 사람들이 여러개 취미에 들 수 있음
    private List<Account> member;

    private String shortDescription;

    @Lob
    private String fullDescription;

    @Lob
    private String banner;

    @ManyToMany
    private List<Hobby> hobby;

    @ManyToMany
    private List<City> city;

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
}
