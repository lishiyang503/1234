<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
#app {
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}
</style>