package com.weather.WeatherApp.controller;

import com.weather.WeatherApp.dto.GeoInfoDTO;
import com.weather.WeatherApp.model.GeoInfo;
import com.weather.WeatherApp.service.IGeoInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/geo-infos")
@RequiredArgsConstructor
public class GeoInfoController {

    private final IGeoInfoService geoInfoService;

    @GetMapping("/get")
    public GeoInfoDTO getWeather(@RequestParam String cityName) {
        return geoInfoService.getWeatherData(cityName);
    }

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
