package com.funmeet.repository;

import com.funmeet.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City,Long> {

    City findByKrCity(String city_kr);
}
