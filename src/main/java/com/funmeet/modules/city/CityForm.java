package com.funmeet.modules.city;


import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CityForm {

    private String cityName;

    public String getEnCity() {
        return cityName.substring(0, cityName.indexOf("("));
    }

    public String getKrCity() {
        return cityName.substring(cityName.indexOf("(") + 1, cityName.indexOf(")"));
    }

    public City getCity() {
        return City.builder().enCity(this.getEnCity())
                .krCity(this.getKrCity())
                .build();
    }

}
