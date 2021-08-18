package com.funmeet.domain;


import lombok.*;
import javax.persistence.*;

@Entity @Getter
@Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"enCity", "krCity"}))
public class City {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String enCity;

    @Column(nullable = false)
    private String krCity;

//    @Column(nullable = true) TODO
//    private String districtList;

    @Override
    public String toString() {
        return String.format("%s(%s)",enCity,krCity);
    }


}
