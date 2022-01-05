package com.funmeet.modules.city;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    @PostConstruct
    public void initLocationInsert() throws IOException {
        if (cityRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("list_district_kr.csv");
            byte[] bytes = resource.getInputStream().readAllBytes();
            String data = new String(bytes, StandardCharsets.UTF_8);

            List<City> cityList = Arrays.stream(data.split("\n")).map(line -> {
                String[] split = line.split(",");
                return City.builder().enCity(split[0]).krCity(split[1].strip()).build();
            }).collect(Collectors.toList());

            cityRepository.saveAll(cityList);
        }
    }

    public City getCityByKrCity(String krCity){
        return cityRepository.findByKrCity(krCity).orElseThrow();
    }

    public List<String> getAllCity(){
        return cityRepository.findAll().stream()
                .map(City::toString)
                .collect(Collectors.toList());
    }
}
