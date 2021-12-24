package com.funmeet.modules.city;


import lombok.*;

import javax.persistence.*;

@Entity @Getter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"enCity", "krCity"}))
public class City {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String enCity;

    @Column(nullable = false)
    private String krCity;

    @Override
    public String toString() {
        return String.format("%s(%s)",enCity,krCity);
    }
}
