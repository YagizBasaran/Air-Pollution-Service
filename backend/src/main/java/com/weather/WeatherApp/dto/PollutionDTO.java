package com.weather.WeatherApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollutionDTO {

    private LocalDate date;

    private double carbonMonoxide; // CO
    private String carbonMonoxideCategory;

    private double ozone; // O3
    private String ozoneCategory;

    private double sulphurDioxide; // SO2
    private String sulphurDioxideCategory;
}
