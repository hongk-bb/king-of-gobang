<template>
  <div class="music-board">
    <div class="img-box">
      <img
        ref="imgRef"
        :class="{ rotate: playStatus }"
        :src="playStatus ? playMusicImg : pauseMusicImg"
        alt="music"
        @click="controlMusic"
      />
      <audio ref="audioRef" :src="musicUrl" loop @play="onPlay"></audio>
    </div>
  </div>
</template>

<script>
import {ref} from "vue"
import playMusicImg from "../assets/images/yinle.png"
import pauseMusicImg from "../assets/images/yinleguanbi.png"
import musicUrl from "../assets/audios/snow.mp4"

export default {
  name: "MusicBoard",
  setup() {
    let audioRef = ref(null)
    let imgRef = ref(null)

    let playStatus = ref(false)

    const controlMusic = () => {
      if (audioRef.value && imgRef.value) {
        if (playStatus.value) {
          audioRef.value.pause()
          playStatus.value = false
        } else {
          audioRef.value.play()
          playStatus.value = true
        }
      }
    }
    return {
      playMusicImg,
      pauseMusicImg,
      musicUrl,
      audioRef,
      controlMusic,
      imgRef,
      playStatus
    }
  }
}
</script>

<style scoped>
.music-board {
  position: fixed;
  bottom: 0;
  right: 0;
  margin: 1vh;
  z-index: 999;
}

/*元素持续旋转关键帧*/
@keyframes rotation {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.img-box {
  width: 10vh;
  height: 10vh;
}

.img-box img:hover {
  border-radius: 50%;
  cursor: pointer;
  transition: box-shadow 300ms;
  box-shadow: 0 0 10px 5px #D78578;
}

.img-box img {
  width: 100%;
  height: 100%;
}

/*旋转类*/
.rotate {
  animation: rotation 3s linear infinite;

  border-radius: 50%;
  cursor: pointer;
  transition: box-shadow 300ms;
  box-shadow: 0 0 10px 5px #FFC05B;
}

@media screen and (max-width: 768px) {
/*  .music-board {
    margin: 1vw;
  }*/

  .img-box {
    width: 8vh;
    height: 8vh;
  }
}
</style>
