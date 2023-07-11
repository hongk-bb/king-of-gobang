<template>
  <NavBar v-show="$store.state.pk.status === 'matching'"/>
  <div
    v-if="$store.state.pk.status !== 'matching'"
  ></div>
  <!--  style="height: 40px"-->
  <router-view/>
  <MusicBoard/>
  <div class="test" ></div>
</template>

<script>
import NavBar from "@/components/NavBar.vue"
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import MusicBoard from "@/components/MusicBoard.vue"

export default {
  components: {
    MusicBoard,
    NavBar
  },
  setup() {

  }
}
</script>

<style>
body {
  background: url("@/assets/images/bg3.jpg") 100% repeat;
}

@media (max-width: 767px) {
  body {
    font-size: 2vh;
  }
}
</style>
