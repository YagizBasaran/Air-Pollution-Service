<script>
import axios from 'axios';
import { defineComponent, ref, computed } from 'vue';

export default defineComponent({
  setup() {
    const cityName = ref('');
    const startDate = ref('');
    const endDate = ref('');
    const weatherData = ref(null);
    const isLoading = ref(false); // Loading state

    const today = new Date();
    const maxEndDate = computed(() => today.toISOString().split('T')[0]);

    const fetchCoordinates = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/geo-infos/get', {
          params: {
            cityName: cityName.value
          }
        });

        const { latitude, longitude } = response.data;

        return { latitude, longitude };
      } catch (error) {
        console.error('Error fetching coordinates:', error);
        throw error;
      }
    };

    const fetchWeatherData = async () => {
      isLoading.value = true; // Start loading
      try {
        const { latitude, longitude } = await fetchCoordinates();

        const formattedStartDate = startDate.value.split('-').reverse().join('-');
        const formattedEndDate = endDate.value.split('-').reverse().join('-');

        const response = await axios.get('http://localhost:8080/api/geo-infos/pollution', {
          params: {
            city: cityName.value,
            startDate: formattedStartDate,
            endDate: formattedEndDate
          }
        });

        weatherData.value = response.data;
      } catch (error) {
        console.error('Error fetching weather data:', error);
      } finally {
        isLoading.value = false; // End loading
      }
    };

    return {
      cityName,
      startDate,
      endDate,
      weatherData,
      isLoading, // Provide loading state
      fetchWeatherData,
      maxEndDate
    };
  }
});
</script>

<template>
  <div class="weather-form">
    <h1>Get Weather Pollution Data</h1>
    <form @submit.prevent="fetchWeatherData">
      <div>
        <label for="cityName">City Name:</label>
        <input type="text" id="cityName" v-model="cityName" required />
      </div>
      <div>
        <label for="start-date">Start Date:</label>
        <input type="date" id="start-date" min="2020-11-27" v-model="startDate" :max="maxEndDate" required />
      </div>
      <div>
        <label for="end-date">End Date:</label>
        <input type="date" id="end-date" v-model="endDate" :max="maxEndDate" min="2020-11-27" required />
      </div>
      <button type="submit" :disabled="isLoading">Get Weather Pollution Data</button>
    </form>

    <!-- Loading Skeleton -->
    <div v-if="isLoading" class="table-skeleton">
      <div class="skeleton-header"></div>
      <div class="skeleton-row">
        <div class="skeleton-cell"></div>
        <div class="skeleton-cell"></div>
        <div class="skeleton-cell"></div>
        <div class="skeleton-cell"></div>
        <div class="skeleton-cell"></div>
      </div>
    </div>

    <!-- Weather Data Table -->
    <div v-if="!isLoading && weatherData">
      <h2>Weather Pollution Data for {{ cityName }}</h2>
      <table>
        <thead>
        <tr>
          <th>Date</th>
          <th>Source</th>
          <th>CO</th>
          <th>O3</th>
          <th>SO2</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="result in weatherData[0].Results" :key="result.Date">
          <td>{{ result.Date }}</td>
          <td>{{ result.Source }}</td>
          <td>{{ result.Categories.CO }}</td>
          <td>{{ result.Categories.O3 }}</td>
          <td>{{ result.Categories.SO2 }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.weather-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.weather-form div {
  margin-bottom: 1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f4f4f4;
}

.table-skeleton {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.skeleton-header, .skeleton-row {
  display: flex;
  padding: 8px;
}

.skeleton-header {
  background: #f4f4f4;
  margin-bottom: 1rem;
}

.skeleton-row {
  border-bottom: 1px solid #ccc;
}

.skeleton-cell {
  flex: 1;
  height: 20px;
  background: #ddd;
  margin-right: 8px;
  border-radius: 4px;
}

.skeleton-cell:last-child {
  margin-right: 0;
}
</style>
