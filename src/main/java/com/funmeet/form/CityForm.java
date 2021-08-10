package com.funmeet.form;

import com.funmeet.domain.City;
import lombok.Data;

@Data
public class CityForm {

    private String cityName;

    public String getEnCity() {
        System.out.println(cityName); // 이 녀석이 null 그러면 cityForm이 제대로 안들어왔다는 것.
                                      // 그 말은 즉, CityForm -> City로 못간것.
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
