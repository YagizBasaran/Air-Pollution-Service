package com.weather.WeatherApp.repository;

import com.weather.WeatherApp.model.GeoInfo;
import com.weather.WeatherApp.model.Pollution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IPollutionRepository extends JpaRepository<Pollution, Integer> {

    //List<Pollution> findByCityAndDateBetween(String cityName, LocalDate startDate, LocalDate endDate);
    Optional<Pollution> findByGeoInfoAndDate(GeoInfo geoInfo, LocalDate date);
}
