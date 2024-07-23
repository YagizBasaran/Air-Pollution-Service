package com.weather.WeatherApp.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "GEOGRAPHICAL INFO", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @Data for above four of them
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeoInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    double latitude;

    double longtitude;

    String name;

}
