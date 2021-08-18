package com.funmeet.domain;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Hobby {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

//    @ManyToMany
//    private List<Reply> replyList; // TODO 회원대화방
}
