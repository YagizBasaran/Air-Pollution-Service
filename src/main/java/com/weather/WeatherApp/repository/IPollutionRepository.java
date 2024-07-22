package com.weather.WeatherApp.repository;

import com.weather.WeatherApp.model.Pollution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPollutionRepository extends JpaRepository<Pollution, Integer> {

    List<Pollution> findByCityAndDateBetween(String cityName, LocalDate startDate, LocalDate endDate);

}
