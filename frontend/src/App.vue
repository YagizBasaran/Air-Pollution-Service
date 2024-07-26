<script setup>
import HelloWorld from './components/WeatherApp.vue'
</script>

<template>
  <div>
    <a href="https://github.com/YagizBasaran/Air-Pollution-Service" target="_blank">
      <img src="./assets/weather_img_1.svg" class="logo" alt="Vite logo" />
    </a>
      <img src="./assets/weather_img_2.svg" class="logo vue" alt="Vue logo" />
  </div>
  <HelloWorld msg="Weather Pollution Data Service" />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em rgba(255, 182, 7, 0.67));
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em rgba(0, 63, 255, 0.67));
}
</style>
