package com.funmeet.form;

import com.funmeet.domain.City;
import lombok.Data;

@Data
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
