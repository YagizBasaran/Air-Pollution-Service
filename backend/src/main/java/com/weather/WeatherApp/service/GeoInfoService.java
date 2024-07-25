package com.weather.WeatherApp.service;

import com.weather.WeatherApp.dto.GeoInfoDTO;
import com.weather.WeatherApp.model.GeoInfo;
import com.weather.WeatherApp.repository.IGeoInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j // for Logging
public class GeoInfoService implements IGeoInfoService{

    @Autowired
    private final IGeoInfoRepository geoInfoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String API_KEY = "4ad7c9811b1eee11febbd7fdf29e363e"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    @Override
    public GeoInfoDTO getWeatherData(String cityName) {
        String url = BASE_URL + "?q=" + cityName + "&appid=" + API_KEY;

        GeoInfoDTO weatherDTO = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(GeoInfoDTO.class)
                .block();

        // Save to database
        if (weatherDTO != null && weatherDTO.getCoord() != null) {
            Optional<GeoInfo> existingGeoInfo = Optional.ofNullable(geoInfoRepository.findByName(cityName));

            // Only save if it does not exist
            if (existingGeoInfo.isEmpty()) {
                GeoInfo weather = new GeoInfo();
                weather.setLongtitude(weatherDTO.getCoord().getLon());
                weather.setLatitude(weatherDTO.getCoord().getLat());
                weather.setName(weatherDTO.getName());
                geoInfoRepository.save(weather);
            }
        }

        return weatherDTO;
    }


    @Override
    public void saveGeoInfo(GeoInfo geoInfo){
        log.info("Inserting geo info");
        geoInfoRepository.save(geoInfo);
    }

    @Override
    public List<GeoInfo> getAllGeoInfo(){
        log.info("Finding all geo info");
        return geoInfoRepository.findAll();
    }

    @Override
    public GeoInfo getGeoInfo(int id){
        log.info("Finding specific GeoInfo by id-{}", id);
        return geoInfoRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteGeoInfo(Integer id){
        log.info("Deleting GeoInfo object by id");

        geoInfoRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GeoInfo not found id: "+ id));

        geoInfoRepository.deleteById(id);

    }

    @Override
    public GeoInfo getGeoInfoByCityName(String cityName) {
        return geoInfoRepository.findByName(cityName);
    }



}
