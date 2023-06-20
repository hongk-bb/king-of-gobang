<template>
  <div class="timeboard" v-if="!$store.state.record.is_record">
    <TimingBoard></TimingBoard>
  </div>
  <div style="height: 1vw" v-if="!$store.state.record.is_record"></div>
  <div style="height: 5vh" v-if="$store.state.record.is_record"></div>
  <div class="playground">
    <GameMap></GameMap>
  </div>
  <div class="switch-night">
    <img
        ref="imgRef"
        @click="clickSwitch"
        class="img-box"
    />
  </div>
</template>

<script>
import GameMap from "@/components/GameMap.vue"
import TimingBoard from "@/components/TimingBoard.vue"

import sunImg from "@/assets/images/sun2.png"
import nightImg from "@/assets/images/night.png"

import {onMounted, onUnmounted, ref} from "vue"

export default {
  components: {
    GameMap,
    TimingBoard,
  },
  setup() {
    const clickSwitch = () => {
      if (isNight() === "true") {
        localStorage.setItem("isNight", "false")
      } else {
        localStorage.setItem("isNight", "true")
      }
      changeImg()
      // console.log("localStorage.getItem('isNight') ",localStorage.getItem("isNight"))
    }

    let imgRef = ref(null)

    const isNight = () => {
      return localStorage.getItem("isNight")
    }

    const changeImg = () => {
      if (isNight() === "true") {
        imgRef.value.src = sunImg
        document.body.style.background = "rgb(30,30,30)";
      } else {
        imgRef.value.src = nightImg
        document.body.style.background = `url(${require('@/assets/images/bg3.jpg')}) 100% repeat`;
      }
    }

    onMounted(() => {
      changeImg()
    })

    onUnmounted(() => {
      document.body.style.background = `url(${require('@/assets/images/bg3.jpg')}) 100% repeat`;
    })

    return {
      clickSwitch,
      imgRef
    }
  }
}
</script>

<style scoped>
div.playground {
  width: 80vw;
  height: calc(80vh - 40px);
  margin: 3vh auto 0;
}

div.timeboard {
  width: 60vw;
  height: 15vh;
  margin: 20px auto 0;
}

@media (max-width: 767px) {
  div.playground {
    width: 90vw;
  }

  div.timeboard {
    width: 90vw;
  }
}

@media (max-width: 400px) {
  div.playground {
    margin-top: -5vh;
  }
}

.switch-night {
  position: fixed;
  bottom: 0;
  left: 0;
  margin: 1vh;
  z-index: 999;

  border-radius: 50%;
  overflow: hidden;
}


.img-box {
  width: 10vh;
  height: 10vh;
}

.img-box img {
  width: 100%;
  height: 100%;
}

.img-box:hover {
  transform: scale(1.3);
  transition: scale 300ms;
}

@media screen and (max-width: 768px) {
  /*.switch-night {
    margin: -1vw;
  }*/
  .img-box {
    width: 8vh;
    height: 8vh;
  }
}

</style>
