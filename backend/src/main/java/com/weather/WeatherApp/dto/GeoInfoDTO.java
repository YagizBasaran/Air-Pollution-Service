package com.weather.WeatherApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoInfoDTO {

    private int id;

    private double latitude;

    private double longtitude;

    private String name;

    // Nested class for coord
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Coord {
        private double lon;
        private double lat;
    }

    private Coord coord;

}
