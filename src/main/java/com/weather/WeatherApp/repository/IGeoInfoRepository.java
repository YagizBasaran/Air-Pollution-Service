package com.weather.WeatherApp.repository;

import com.weather.WeatherApp.model.GeoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGeoInfoRepository extends JpaRepository<GeoInfo, Integer> {

    //can define your own QUERIES

}
