package com.funmeet.modules.city;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
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
