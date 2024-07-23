package com.weather.WeatherApp.service;

import com.weather.WeatherApp.dto.PollutionDTO;
import com.weather.WeatherApp.exceptions.InvalidDateException;
import com.weather.WeatherApp.model.GeoInfo;
import com.weather.WeatherApp.model.Pollution;
import com.weather.WeatherApp.repository.IPollutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j // for Logging
public class PollutionService implements IPollutionService {

    @Autowired
    private final IPollutionRepository pollutionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String API_KEY = "4ad7c9811b1eee11febbd7fdf29e363e";
    private static final String AIR_POLLUTION_URL = "http://api.openweathermap.org/data/2.5/air_pollution/history";

    @Override
    public PollutionDTO getAirPollutionData(double latitude, double longitude, long startUnixTime, long endUnixTime) {
        String url = AIR_POLLUTION_URL + "?lat=" + latitude + "&lon=" + longitude + "&start=" + startUnixTime + "&end=" + endUnixTime + "&appid=" + API_KEY;

        Map<String, Object> response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("list") == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Air pollution data not found");
        }

        double totalCarbonMonoxide = 0;
        double totalOzone = 0;
        double totalSulphurDioxide = 0;
        int count = 0;

        List<Object> pollutionDataList = (List<Object>) response.get("list");

        for (Object item : pollutionDataList) {
            Map<String, Object> airPollutionData = (Map<String, Object>) item;
            Map<String, Number> components = (Map<String, Number>) airPollutionData.get("components");

            totalCarbonMonoxide += components.getOrDefault("co", 0.0).doubleValue();
            totalOzone += components.getOrDefault("o3", 0.0).doubleValue();
            totalSulphurDioxide += components.getOrDefault("so2", 0.0).doubleValue();
            count++;
        }

        double avgCarbonMonoxide = totalCarbonMonoxide / count;
        double avgOzone = totalOzone / count;
        double avgSulphurDioxide = totalSulphurDioxide / count;

        String carbonMonoxideCategory = categorizeCarbonMonoxide(avgCarbonMonoxide);
        String ozoneCategory = categorizeOzone(avgOzone);
        String sulphurDioxideCategory = categorizeSulphurDioxide(avgSulphurDioxide);

        return PollutionDTO.builder()
                .carbonMonoxide(avgCarbonMonoxide)
                .carbonMonoxideCategory(carbonMonoxideCategory)
                .ozone(avgOzone)
                .ozoneCategory(ozoneCategory)
                .sulphurDioxide(avgSulphurDioxide)
                .sulphurDioxideCategory(sulphurDioxideCategory)
                .build();
    }


    private String categorizeCarbonMonoxide(double carbonMonoxide) {
        if (carbonMonoxide > 34) {
            return "Hazardous";
        } else if (carbonMonoxide > 17) {
            return "Severe";
        } else if (carbonMonoxide > 9) {
            return "Poor";
        } else if (carbonMonoxide > 2) {
            return "Moderate";
        } else if (carbonMonoxide > 1) {
            return "Satisfactory";
        } else {
            return "Good";
        }
    }

    private String categorizeOzone(double ozone) {
        if (ozone > 748) {
            return "Hazardous";
        } else if (ozone > 208) {
            return "Severe";
        } else if (ozone > 168) {
            return "Poor";
        } else if (ozone > 100) {
            return "Moderate";
        } else if (ozone > 50) {
            return "Satisfactory";
        } else {
            return "Good";
        }
    }

    private String categorizeSulphurDioxide(double sulphurDioxide) {
        if (sulphurDioxide > 1600) {
            return "Hazardous";
        } else if (sulphurDioxide > 800) {
            return "Severe";
        } else if (sulphurDioxide > 380) {
            return "Poor";
        } else if (sulphurDioxide > 80) {
            return "Moderate";
        } else if (sulphurDioxide > 40) {
            return "Satisfactory";
        } else {
            return "Good";
        }
    }

    public List<Map<String, Object>> fetchAndSavePollutionData(GeoInfo geoInfo, LocalDate startDate, LocalDate endDate) {

        // Define the minimum valid date
        LocalDate minValidDate = LocalDate.parse("27-11-2020", DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Check if startDate is before the minimum valid date
        if (startDate.isBefore(minValidDate)) {
            throw new InvalidDateException("Start date cannot be before 27-11-2020");
        }

        List<Map<String, Object>> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());

        for (LocalDate date : dates) {
            long startUnixTime = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            long endUnixTime = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;

            Optional<Pollution> existingPollution = pollutionRepository.findByGeoInfoAndDate(geoInfo, date);
            if (existingPollution.isPresent()) {
                // If data exists, skip saving but include it in the results
                Pollution pollution = existingPollution.get();
                results.add(Map.of(
                        "Date", date.format(formatter),
                        "Categories", Map.of(
                                "CO", categorizeCarbonMonoxide(pollution.getCarbonMonoxide()),
                                "O3", categorizeOzone(pollution.getOzone()),
                                "SO2", categorizeSulphurDioxide(pollution.getSulphurDioxide())
                        )
                ));
                continue;
            }

            PollutionDTO dto = getAirPollutionData(geoInfo.getLatitude(), geoInfo.getLongtitude(),startUnixTime,endUnixTime);

            Pollution pollution = Pollution.builder()
                    .geoInfo(geoInfo)
                    .date(date)
                    .carbonMonoxide(dto.getCarbonMonoxide())
                    .ozone(dto.getOzone())
                    .sulphurDioxide(dto.getSulphurDioxide())
                    .category(dto.getCarbonMonoxideCategory() + ", " + dto.getOzoneCategory() + ", " + dto.getSulphurDioxideCategory())
                    .build();

            pollutionRepository.save(pollution);

            results.add(Map.of(
                    "Date", date.format(formatter),
                    "Categories", Map.of(
                            "CO", dto.getCarbonMonoxideCategory(),
                            "O3", dto.getOzoneCategory(),
                            "SO2", dto.getSulphurDioxideCategory()
                    )
            ));
        }

        return results;
    }
}
