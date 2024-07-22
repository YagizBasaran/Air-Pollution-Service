package com.weather.WeatherApp.service;

import com.weather.WeatherApp.dto.GeoInfoDTO;
import com.weather.WeatherApp.dto.PollutionDTO;
import com.weather.WeatherApp.model.GeoInfo;

import java.util.List;

public interface IGeoInfoService {

    GeoInfo getGeoInfoByCityName(String cityName);

    public void saveGeoInfo(GeoInfo geoInfo);

    public List<GeoInfo> getAllGeoInfo();

    public GeoInfo getGeoInfo(int id);

    public void deleteGeoInfo(Integer id);

    public GeoInfoDTO getWeatherData(String url);

}
