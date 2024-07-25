package com.weather.WeatherApp.service;

import com.weather.WeatherApp.dto.PollutionDTO;
import com.weather.WeatherApp.model.GeoInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IPollutionService {
    List<PollutionDTO> getAirPollutionData(double latitude, double longitude, long startUnixTime, long endUnixTime);

    List<Map<String, Object>> fetchAndSavePollutionData(GeoInfo geoInfo, LocalDate start, LocalDate end);
}
