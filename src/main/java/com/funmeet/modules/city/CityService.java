package com.funmeet.modules.city;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
            Resource resource = new ClassPathResource("list_district_kr.csv");
            List<City> cityList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return City.builder().enCity(split[0]).krCity(split[1]).build();
                    }).collect(Collectors.toList());
            cityRepository.saveAll(cityList);
        }
    }

    public City getCityByKrCity(String krCity){
        return cityRepository.findByKrCity(krCity);
    }

    public List<String> getAllCity(){
        return cityRepository.findAll().stream().map(City::toString).collect(Collectors.toList());
    }
}
