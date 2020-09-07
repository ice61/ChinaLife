<template>
  <div id="app" style="height: 100%;width: 100%;position: absolute;">
    <transition name="el-zoom-in-center" mode="out-in">
      <router-view/>
    </transition>
  </div>
</template>

<script>
  export default {
    name: 'App'
  }
</script>

<style>
  body {
    margin: 0;
  }
</style>
