package com.weather.WeatherApp.repository;

import com.weather.WeatherApp.model.Pollution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPollutionRepository extends JpaRepository<Pollution, Integer> {
}
