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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    public List<PollutionDTO> getAirPollutionData(double latitude, double longitude, long startUnixTime, long endUnixTime) {
        String url = AIR_POLLUTION_URL + "?lat=" + latitude + "&lon=" + longitude + "&start=" + startUnixTime + "&end=" + endUnixTime + "&appid=" + API_KEY;

        // Read response as a Map
        Map<String, Object> response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("list") == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Air pollution data not found");
        }

        // Extract list of pollution data from the response
        List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");

        Map<LocalDate, List<Map<String, Object>>> dailyDataMap = new HashMap<>();

        // Organize data by day
        for (Map<String, Object> item : list) {
            long timestamp = toLong(item.get("dt"));
            LocalDate date = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("UTC")).toLocalDate();

            dailyDataMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        List<PollutionDTO> dayByDayDataOfPollution = new ArrayList<>();

        for (Map.Entry<LocalDate, List<Map<String, Object>>> entry : dailyDataMap.entrySet()) {
            LocalDate date = entry.getKey();
            List<Map<String, Object>> dailyItems = entry.getValue();

            double totalCarbonMonoxide = 0;
            double totalOzone = 0;
            double totalSulphurDioxide = 0;

            for (Map<String, Object> item : dailyItems) {
                Map<String, Object> components = (Map<String, Object>) item.get("components");

                double carbonMonoxide = toDouble(components.get("co"));
                double ozone = toDouble(components.get("o3"));
                double sulphurDioxide = toDouble(components.get("so2"));

                totalCarbonMonoxide += carbonMonoxide;
                totalOzone += ozone;
                totalSulphurDioxide += sulphurDioxide;
            }

            int count = dailyItems.size();

            double avgCarbonMonoxide = totalCarbonMonoxide / count;
            double avgOzone = totalOzone / count;
            double avgSulphurDioxide = totalSulphurDioxide / count;

            String carbonMonoxideCategory = categorizeCarbonMonoxide(avgCarbonMonoxide);
            String ozoneCategory = categorizeOzone(avgOzone);
            String sulphurDioxideCategory = categorizeSulphurDioxide(avgSulphurDioxide);

            PollutionDTO tempPollutionDTO = PollutionDTO.builder()
                    .date(date)
                    .carbonMonoxide(avgCarbonMonoxide)
                    .carbonMonoxideCategory(carbonMonoxideCategory)
                    .ozone(avgOzone)
                    .ozoneCategory(ozoneCategory)
                    .sulphurDioxide(avgSulphurDioxide)
                    .sulphurDioxideCategory(sulphurDioxideCategory)
                    .build();

            dayByDayDataOfPollution.add(tempPollutionDTO);
        }

        return dayByDayDataOfPollution;
    }


    // Helper method to convert Number to Double
    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException("Value is not a number");
        }
    }
    // Helper method to convert Object to long
    private long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalArgumentException("Value is not a number");
        }
    }



    public List<Map<String, Object>> fetchAndSavePollutionData(GeoInfo geoInfo, LocalDate startDate, LocalDate endDate) {

        // Define the minimum valid date
        LocalDate minValidDate = LocalDate.parse("27-11-2020", DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Check if startDate is before the minimum valid date
        if (startDate.isBefore(minValidDate)) {
            throw new InvalidDateException("Start date cannot be before 27-11-2020");
        }

        long startUnixTime = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long endUnixTime = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;

        List<Map<String, Object>> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<PollutionDTO> pollutionDTOList = getAirPollutionData(geoInfo.getLatitude(), geoInfo.getLongtitude(),startUnixTime,endUnixTime);

        for( PollutionDTO tmp : pollutionDTOList){
            Pollution pollution = Pollution.builder()
                    .geoInfo(geoInfo)
                    .date(tmp.getDate())
                    .carbonMonoxide(tmp.getCarbonMonoxide())
                    .sulphurDioxide(tmp.getSulphurDioxide())
                    .ozone(tmp.getOzone())
                    .category(tmp.getCarbonMonoxideCategory() + ", " + tmp.getOzoneCategory() + ", " + tmp.getSulphurDioxideCategory())
                    .build();

            pollutionRepository.save(pollution);

            Map<String, String> categories = new LinkedHashMap<>();
            categories.put("CO", tmp.getCarbonMonoxideCategory());
            categories.put("O3", tmp.getOzoneCategory());
            categories.put("SO2", tmp.getSulphurDioxideCategory());

            // Add results with Date before Categories
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("Date", tmp.getDate().format(formatter));
            result.put("Categories", categories);

            results.add(result);
        }

        // Sort results by date in ascending order
        results.sort((r1, r2) -> {
            LocalDate date1 = LocalDate.parse((String) r1.get("Date"), formatter);
            LocalDate date2 = LocalDate.parse((String) r2.get("Date"), formatter);
            return date1.compareTo(date2);
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("City", geoInfo.getName());
        response.put("Results", results);

        return Collections.singletonList(response);
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
}
