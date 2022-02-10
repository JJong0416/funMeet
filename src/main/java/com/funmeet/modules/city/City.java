package com.funmeet.modules.city;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity @Getter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class City {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String enCity;

    @Column(nullable = false)
    private String krCity;

    @Override
    public String toString() {
        return String.format("%s(%s)", enCity, krCity);
    }
}

