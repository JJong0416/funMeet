package com.funmeet.repository;

import com.funmeet.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CityRepository extends JpaRepository<City,Long> {

    City findByKrCity(String city_kr);
}
