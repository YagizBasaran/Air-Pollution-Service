package com.weather.WeatherApp.controller;

import com.weather.WeatherApp.dto.GeoInfoDTO;
import com.weather.WeatherApp.dto.PollutionDTO;
import com.weather.WeatherApp.model.GeoInfo;
import com.weather.WeatherApp.service.IGeoInfoService;
import com.weather.WeatherApp.service.IPollutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/geo-infos")
@RequiredArgsConstructor
public class GeoInfoController {

    private final IGeoInfoService geoInfoService;
    private final IPollutionService pollutionService;

    @GetMapping("/get")
    public GeoInfoDTO getWeather(@RequestParam String cityName) {
        return geoInfoService.getWeatherData(cityName);
    }

    @GetMapping("/pollution")
    public ResponseEntity getPollutionData(
            @RequestParam String city,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        GeoInfo geoInfo = geoInfoService.getGeoInfoByCityName(city);
        if (geoInfo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "City not found"));
        }



        return ResponseEntity.ok(pollutionService.fetchAndSavePollutionData(geoInfo, start, end));
    }


    //FOR JUST CHECKING
    @GetMapping
    public ResponseEntity<List<GeoInfo>> getGeoInfos(){
        return ResponseEntity.ok(geoInfoService.getAllGeoInfo());
    }

    @PostMapping
    public ResponseEntity<GeoInfo> createGeoInfo(@RequestBody GeoInfo geoInfo){
        geoInfoService.saveGeoInfo(geoInfo);
        return new ResponseEntity<>(geoInfo, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Integer id){
        geoInfoService.deleteGeoInfo(id);
        return ResponseEntity.ok(true);
    }


}
