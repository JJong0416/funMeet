package com.funmeet.modules.hobby;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity @Getter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class Hobby {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;
}
