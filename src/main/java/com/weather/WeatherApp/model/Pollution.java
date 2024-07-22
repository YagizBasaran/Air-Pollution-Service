package com.weather.WeatherApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "DAILY_AIR_POLLUTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pollution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /*@ManyToOne
    @JoinColumn(name = "geo_info_id")
    private GeoInfo geoInfo;*/

    private LocalDate date;

    private double carbonMonoxide; //CO

    private double ozone; //O3

    private double sulphurDioxide; //SO2

    private String category;
}

