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
public class Meet {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account account;

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

    @OneToMany(mappedBy = "meet")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private MeetType meetType;

}
