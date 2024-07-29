# Air Pollution Data RESTful Service 🌤
This project involves developing a RESTful application using **Java Spring Boot** to fetch and store historical air pollution data from the *OpenWeatherMap API* based on city name and date range. The application categorizes the pollutants using the air quality index categories and supports querying historical pollution data for specific cities. Additionally, front-end is available developed with **Vue**.

## Features ✔
- Fetches city coordinates using the Geocoding API from OpenWeatherMap.
- Retrieves historical air pollution data using the Historical Air Pollution API from OpenWeatherMap.
- Categorizes pollutant concentrations into air quality categories (Good, Satisfactory, Moderate, Poor, Severe, Hazardous).
- Supports querying by city name and date range.
- Only fetches data from the API for dates not present in the database.
- Logs whether data was retrieved from the database or API.
- Allows data deletion by city name.

# Requirements
You only need [Docker](https://www.docker.com/) to run this application, everything is already sorted for you.
- ***One important reminder***, files `PollutionService.java` and `GeoInfoService.java` contains `API_KEY` variable which must be obtained from https://openweathermap.org/ with free registeration and typed to mentioned places.

# Setup 🔧
1. Download the repository folder (don't forget to un-zip it) and Docker, open it.
2. From "command prompt" go to folder where docker-compose.yaml file resists (e.g *C:\Users\Downloads\Air-Pollution-Service*).
3. Type in `docker-compose -f .\docker-compose.yaml up --build -d`
4. You are done! Go to *"http://localhost:4000/"* and start using the application.

# H2 Database
In order to use H2 Database that comes with the Spring Boot for this application: 
- Go to link *"http://localhost:8080/h2-console/*"
- **JDBC URL:** jdbc:h2:mem:db_example
- **Username:** admin
- **Password:** 123

# Postman and Endpoints
In order to use Postman, you can use the requests given below:
1. Custom city save:
   - **POST** `localhost:8080/api/geo-infos`
     - {
    "latitude": 343.31,
    "longtitude": 917,
    "name":"ExampleCity"}

2. Delete city:
   - **DELETE** `localhost:8080/api/geo-infos/delete/{city_id}`

3. See informations:
   - **GET** `localhost:8080/api/geo-infos`

4. Get your desired city from internet:
   - **GET** `localhost:8080/api/geo-infos/get?cityName={yourCityName}`

5. Get pollution data from internet:
   - **GET** `localhost:8080/api/geo-infos/pollution?city={yourCityName}&startDate={dd-mm-yyyy}&endDate={dd-mm-yyyy}`

# Screenshots
<img width="960" alt="airservice1" src="https://github.com/user-attachments/assets/0e1c0092-a152-41e3-9caa-58e4f82c7a11">
<img width="960" alt="airservice2" src="https://github.com/user-attachments/assets/83a353f5-ce6d-47f5-bac8-7e337d9371a2">

# Contact 
1903yagizbasaran@gmail.com
